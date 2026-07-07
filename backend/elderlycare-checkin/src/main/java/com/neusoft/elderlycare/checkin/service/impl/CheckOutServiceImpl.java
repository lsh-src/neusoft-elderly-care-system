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
import com.neusoft.elderlycare.entity.CheckOut;
import com.neusoft.elderlycare.entity.Customer;
import com.neusoft.elderlycare.feign.BedFeignClient;
import com.neusoft.elderlycare.feign.CustomerFeignClient;
import com.neusoft.elderlycare.checkin.mapper.CheckOutMapper;
import com.neusoft.elderlycare.checkin.service.CheckInService;
import com.neusoft.elderlycare.checkin.service.CheckOutService;
import com.neusoft.elderlycare.util.NumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckOutServiceImpl extends ServiceImpl<CheckOutMapper, CheckOut> implements CheckOutService {
    private final CustomerFeignClient customerFeignClient;
    private final BedFeignClient bedFeignClient;
    private final CheckInService checkInService;
    private final CheckOutMapper checkOutMapper;

    @Override
    public boolean save(CheckOut entity) {
        entity.setCheckoutNo(NumberGenerator.next("CO", prefix -> baseMapper.selectMaxByPrefix(prefix)));
        return super.save(entity);
    }

    @Override
    public boolean checkout(CheckOut entity) {
        if (entity.getCustomerId() == null) {
            throw new BusinessException("客户ID不能为空");
        }

        // 1. Feign 校验（事务外）
        Customer c = customerFeignClient.getById(entity.getCustomerId()).getData();
        if (c == null) throw new BusinessException("客户不存在");

        boolean hasActiveCheckIn = checkInService.count(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getCustomerId, entity.getCustomerId())) > 0;
        if (!hasActiveCheckIn) {
            if (c.getCheckedIn() != null && c.getCheckedIn() == 1) {
                c.setCheckedIn(0);
                try { customerFeignClient.updateById(c.getId(), c); } catch (Exception e) { log.error("修复残留状态失败: {}", e.getMessage()); }
            }
            throw new BusinessException("该客户未入住，不能办理退住");
        }

        // 2. 数据库保存（短事务）
        entity.setCheckoutNo(NumberGenerator.next("CO", prefix -> baseMapper.selectMaxByPrefix(prefix)));
        boolean saved = doSave(entity);

        // 3. Feign 更新客户状态 + 释放床位（事务外）
        if (saved) {
            try {
                c.setCheckedIn(0);
                customerFeignClient.updateById(c.getId(), c);
            } catch (Exception e) {
                log.error("更新客户入住状态失败: {}", e.getMessage());
            }
            // 释放床位：查找该客户对应的入住记录，释放床位
            try {
                var activeCheckIn = checkInService.list(new LambdaQueryWrapper<CheckIn>()
                        .eq(CheckIn::getCustomerId, entity.getCustomerId())).stream().findFirst().orElse(null);
                if (activeCheckIn != null && activeCheckIn.getBedId() != null) {
                    Bed bed = bedFeignClient.getById(activeCheckIn.getBedId()).getData();
                    if (bed != null) {
                        bed.setCustomerId(null);
                        bed.setStatus("空闲");
                        bedFeignClient.updateById(bed.getId(), bed);
                    }
                }
            } catch (Exception e) {
                log.error("释放床位失败: {}", e.getMessage());
            }
        }
        return saved;
    }

    @Transactional
    public boolean doSave(CheckOut entity) {
        return super.save(entity);
    }

    @Override
    public IPage<CheckOut> pageWithCustomer(PageQuery query) {
        Page<CheckOut> page = new Page<>(query.getCurrent(), query.getSize());
        return checkOutMapper.selectCheckOutPage(page, query.getKeyword());
    }

    @Override
    public boolean removeById(Serializable id) {
        CheckOut checkOut = getById(id);
        boolean removed = doRemove(id);
        if (removed && checkOut != null && checkOut.getCustomerId() != null) {
            try {
                boolean hasActiveCheckIn = checkInService.count(new LambdaQueryWrapper<CheckIn>()
                        .eq(CheckIn::getCustomerId, checkOut.getCustomerId())) > 0;
                if (hasActiveCheckIn) {
                    Customer c = customerFeignClient.getById(checkOut.getCustomerId()).getData();
                    if (c != null) {
                        c.setCheckedIn(1);
                        customerFeignClient.updateById(c.getId(), c);
                    }
                }
            } catch (Exception e) {
                log.error("恢复客户入住状态失败: {}", e.getMessage());
            }
        }
        return removed;
    }

    @Transactional
    public boolean doRemove(Serializable id) {
        return super.removeById(id);
    }
}
