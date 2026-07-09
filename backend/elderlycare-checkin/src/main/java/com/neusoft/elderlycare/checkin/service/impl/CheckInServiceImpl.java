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
import com.neusoft.elderlycare.service.StateSyncService;
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
    private final StateSyncService stateSyncService;

    @Override
    public boolean register(CheckIn entity) {
        // 1. Feign 校验（事务外）
        if (entity.getCustomerId() != null) {
            Customer c = customerFeignClient.getById(entity.getCustomerId()).getData();
            if (c == null) throw new BusinessException("客户不存在");

            // 检查是否有「未退住」的入住记录（LEFT JOIN check_out 为空 = 未退住）
            boolean hasActiveCheckIn = checkInMapper.countActiveCheckIn(entity.getCustomerId()) > 0;
            if (hasActiveCheckIn) {
                throw new BusinessException("该客户当前已入住，不能重复入住");
            }

            // 修复残留状态（带重试）
            if (c.getCheckedIn() != null && c.getCheckedIn() == 1) {
                stateSyncService.fixCustomerResidualState(c.getId());
            }
        }

        if (entity.getBedId() != null) {
            Bed bed = bedFeignClient.getById(entity.getBedId()).getData();
            if (bed == null) throw new BusinessException("床位不存在");

            // 检查床位是否有「未退住」的入住记录
            boolean bedOccupied = checkInMapper.countActiveCheckInByBed(entity.getBedId()) > 0;
            if (bedOccupied) {
                throw new BusinessException("该床位已被占用，无法分配");
            }

            // 修复床位残留状态（带重试）
            if (!"空闲".equals(bed.getStatus())) {
                stateSyncService.fixBedResidualState(bed.getId());
            }
        }

        // 2. 本地事务：生成编号 + 保存入住记录
        entity.setRegisterNo(NumberGenerator.next("CI", prefix -> baseMapper.selectMaxByPrefix(prefix)));
        boolean saved = doSave(entity);

        // 3. 跨服务状态同步（带自动重试，失败不回滚本地事务）
        if (saved) {
            syncAfterCheckIn(entity);
        }
        return saved;
    }

    @Transactional
    public boolean doSave(CheckIn entity) {
        return super.save(entity);
    }

    /**
     * 入住后的跨服务状态同步 — 使用 StateSyncService 自动重试
     */
    private void syncAfterCheckIn(CheckIn entity) {
        // 更新客户入住状态
        if (entity.getCustomerId() != null) {
            try {
                stateSyncService.markCustomerCheckedIn(entity.getCustomerId(), entity.getCheckInDate());

                // 发送 MQ 事件
                Customer c = customerFeignClient.getById(entity.getCustomerId()).getData();
                if (c != null) {
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
                log.error("客户状态同步或MQ发送失败: {}", e.getMessage());
            }
        }

        // 更新床位状态
        if (entity.getBedId() != null) {
            try {
                stateSyncService.assignBed(entity.getBedId(), entity.getCustomerId());
            } catch (Exception e) {
                log.error("床位状态同步失败: {}", e.getMessage());
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
                stateSyncService.markCustomerCheckedOut(checkIn.getCustomerId());
            }
        } catch (Exception e) {
            log.error("释放客户状态失败: {}", e.getMessage());
        }
        try {
            long bedRemaining = count(new LambdaQueryWrapper<CheckIn>()
                    .eq(CheckIn::getBedId, checkIn.getBedId()));
            if (bedRemaining == 0 && checkIn.getBedId() != null) {
                stateSyncService.releaseBed(checkIn.getBedId());
            }
        } catch (Exception e) {
            log.error("释放床位失败: {}", e.getMessage());
        }
    }
}
