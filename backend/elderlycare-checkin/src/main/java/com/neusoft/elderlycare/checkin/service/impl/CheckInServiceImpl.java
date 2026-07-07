package com.neusoft.elderlycare.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.io.Serializable;
import com.neusoft.elderlycare.common.BusinessException;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.Bed;
import com.neusoft.elderlycare.entity.CheckIn;
import com.neusoft.elderlycare.entity.Customer;
import com.neusoft.elderlycare.feign.BedFeignClient;
import com.neusoft.elderlycare.feign.CustomerFeignClient;
import com.neusoft.elderlycare.checkin.mapper.CheckInMapper;
import com.neusoft.elderlycare.checkin.service.CheckInService;
import com.neusoft.elderlycare.mq.AiAnalysisEvent;
import com.neusoft.elderlycare.mq.BusinessEvent;
import com.neusoft.elderlycare.mq.MqConstants;
import com.neusoft.elderlycare.util.NumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements CheckInService {
    private final BedFeignClient bedFeignClient;
    private final CustomerFeignClient customerFeignClient;
    private final CheckInMapper checkInMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public boolean register(CheckIn entity) {
        // 1. 先通过 Feign 调用校验（事务外，不持锁）
        if (entity.getCustomerId() != null) {
            Customer c = customerFeignClient.getById(entity.getCustomerId()).getData();
            if (c == null) throw new BusinessException("客户不存在");

            boolean hasActiveCheckIn = count(new LambdaQueryWrapper<CheckIn>()
                    .eq(CheckIn::getCustomerId, entity.getCustomerId())) > 0;
            if (hasActiveCheckIn) {
                throw new BusinessException("该客户已入住，不能重复入住");
            }

            // 修复残留状态
            if (c.getCheckedIn() != null && c.getCheckedIn() == 1) {
                c.setCheckedIn(0);
                customerFeignClient.updateById(c.getId(), c);
            }
        }

        if (entity.getBedId() != null) {
            Bed bed = bedFeignClient.getById(entity.getBedId()).getData();
            if (bed == null) throw new BusinessException("床位不存在");

            boolean bedOccupied = count(new LambdaQueryWrapper<CheckIn>()
                    .eq(CheckIn::getBedId, entity.getBedId())) > 0;
            if (bedOccupied) {
                throw new BusinessException("该床位已被占用，无法分配");
            }

            if (!"空闲".equals(bed.getStatus())) {
                bed.setCustomerId(null);
                bed.setStatus("空闲");
                bedFeignClient.updateById(bed.getId(), bed);
            }
        }

        // 2. 生成编号并保存入住记录（短事务，快速释放锁）
        entity.setRegisterNo(NumberGenerator.next("CI", prefix -> baseMapper.selectMaxByPrefix(prefix)));
        boolean saved = doSave(entity);

        // 3. Feign 调用更新客户和床位状态（事务外，不持锁）
        if (saved) {
            updateCustomerAndBed(entity);
        }
        return saved;
    }

    @Transactional
    public boolean doSave(CheckIn entity) {
        return super.save(entity);
    }

    private void updateCustomerAndBed(CheckIn entity) {
        if (entity.getCustomerId() != null) {
            try {
                Customer c = customerFeignClient.getById(entity.getCustomerId()).getData();
                if (c != null) {
                    c.setCheckedIn(1);
                    if (entity.getCheckInDate() != null) {
                        c.setCheckInDate(entity.getCheckInDate());
                    }
                    customerFeignClient.updateById(c.getId(), c);

                    AiAnalysisEvent aiEvent = AiAnalysisEvent.of(
                            c.getId(), c.getName(), "HEALTH_ASSESSMENT");
                    rabbitTemplate.convertAndSend(MqConstants.EXCHANGE_AI,
                            MqConstants.KEY_AI_ANALYZE, aiEvent);

                    BusinessEvent<CheckIn> bizEvent = BusinessEvent.of(
                            "CHECKIN_CREATED", entity, "elderlycare-checkin");
                    rabbitTemplate.convertAndSend(MqConstants.EXCHANGE_BUSINESS,
                            MqConstants.KEY_CHECKIN_CREATED, bizEvent);

                    log.info("[MQ] 入住事件已发送: customer={}, checkIn={}", c.getName(), entity.getRegisterNo());
                }
            } catch (Exception e) {
                log.error("更新客户状态或发送MQ失败: {}", e.getMessage());
            }
        }
        if (entity.getBedId() != null) {
            try {
                Bed bed = bedFeignClient.getById(entity.getBedId()).getData();
                if (bed != null) {
                    bed.setCustomerId(entity.getCustomerId());
                    bed.setStatus("已入住");
                    bedFeignClient.updateById(bed.getId(), bed);
                }
            } catch (Exception e) {
                log.error("更新床位状态失败: {}", e.getMessage());
            }
        }
    }

    @Override
    public IPage<CheckIn> pageWithRelation(PageQuery query) {
        Page<CheckIn> page = new Page<>(query.getCurrent(), query.getSize());
        return checkInMapper.selectCheckInPage(page, query.getKeyword());
    }

    @Override
    public boolean removeById(Serializable id) {
        CheckIn checkIn = getById(id);
        boolean removed = doRemove(id);
        if (removed && checkIn != null) {
            releaseCustomerAndBed(checkIn);
        }
        return removed;
    }

    @Transactional
    public boolean doRemove(Serializable id) {
        return super.removeById(id);
    }

    private void releaseCustomerAndBed(CheckIn checkIn) {
        try {
            long remaining = count(new LambdaQueryWrapper<CheckIn>()
                    .eq(CheckIn::getCustomerId, checkIn.getCustomerId()));
            if (remaining == 0 && checkIn.getCustomerId() != null) {
                Customer c = customerFeignClient.getById(checkIn.getCustomerId()).getData();
                if (c != null) {
                    c.setCheckedIn(0);
                    customerFeignClient.updateById(c.getId(), c);
                }
            }
        } catch (Exception e) {
            log.error("释放客户状态失败: {}", e.getMessage());
        }
        try {
            long bedRemaining = count(new LambdaQueryWrapper<CheckIn>()
                    .eq(CheckIn::getBedId, checkIn.getBedId()));
            if (bedRemaining == 0 && checkIn.getBedId() != null) {
                Bed bed = bedFeignClient.getById(checkIn.getBedId()).getData();
                if (bed != null) {
                    bed.setCustomerId(null);
                    bed.setStatus("空闲");
                    bedFeignClient.updateById(bed.getId(), bed);
                }
            }
        } catch (Exception e) {
            log.error("释放床位状态失败: {}", e.getMessage());
        }
    }
}
