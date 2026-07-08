package com.neusoft.elderlycare.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.io.Serializable;
import com.neusoft.elderlycare.common.BusinessException;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.CheckIn;
import com.neusoft.elderlycare.entity.CheckOut;
import com.neusoft.elderlycare.entity.Customer;
import com.neusoft.elderlycare.feign.CustomerFeignClient;
import com.neusoft.elderlycare.checkin.mapper.CheckOutMapper;
import com.neusoft.elderlycare.checkin.service.CheckInService;
import com.neusoft.elderlycare.checkin.service.CheckOutService;
import com.neusoft.elderlycare.service.StateSyncService;
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
    private final CheckInService checkInService;
    private final CheckOutMapper checkOutMapper;
    private final StateSyncService stateSyncService;

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
                // 修复残留状态（带重试）
                stateSyncService.fixCustomerResidualState(c.getId());
            }
            throw new BusinessException("该客户未入住，不能办理退住");
        }

        // 2. 本地事务：保存退住记录
        entity.setCheckoutNo(NumberGenerator.next("CO", prefix -> baseMapper.selectMaxByPrefix(prefix)));
        boolean saved = doSave(entity);

        // 3. 跨服务状态同步（带自动重试，失败不回滚本地事务）
        if (saved) {
            try {
                stateSyncService.markCustomerCheckedOut(entity.getCustomerId());
            } catch (Exception e) {
                log.error("客户退住状态同步失败: {}", e.getMessage());
            }

            // 释放床位
            try {
                var activeCheckIn = checkInService.list(new LambdaQueryWrapper<CheckIn>()
                        .eq(CheckIn::getCustomerId, entity.getCustomerId())).stream().findFirst().orElse(null);
                if (activeCheckIn != null && activeCheckIn.getBedId() != null) {
                    stateSyncService.releaseBed(activeCheckIn.getBedId());
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
                    stateSyncService.markCustomerCheckedIn(checkOut.getCustomerId(), null);
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
