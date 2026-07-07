package com.neusoft.elderlycare.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.elderlycare.common.BusinessException;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.CheckIn;
import com.neusoft.elderlycare.entity.Outing;
import com.neusoft.elderlycare.checkin.mapper.OutingMapper;
import com.neusoft.elderlycare.checkin.service.CheckInService;
import com.neusoft.elderlycare.checkin.service.OutingService;
import com.neusoft.elderlycare.util.NumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutingServiceImpl extends ServiceImpl<OutingMapper, Outing> implements OutingService {
    private final OutingMapper mapper;
    private final CheckInService checkInService;

    @Override
    public boolean returnBack(Long id) {
        Outing outing = getById(id);
        if (outing == null) {
            throw new BusinessException("外出记录不存在");
        }
        if ("已返回".equals(outing.getStatus())) {
            throw new BusinessException("该外出记录已返回，不能重复操作");
        }
        outing.setActualReturnTime(LocalDateTime.now());
        outing.setStatus("已返回");
        return updateById(outing);
    }

    @Override
    public IPage<Outing> pageWithCustomer(PageQuery query) {
        Page<Outing> page = new Page<>(query.getCurrent(), query.getSize());
        return mapper.selectOutingPage(page, query.getKeyword());
    }

    @Override
    public boolean save(Outing entity) {
        // 校验：外出的人必须是已入住的客户
        if (entity.getCustomerId() == null) {
            throw new BusinessException("客户ID不能为空");
        }
        boolean hasActiveCheckIn = checkInService.count(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getCustomerId, entity.getCustomerId())) > 0;
        if (!hasActiveCheckIn) {
            throw new BusinessException("该客户未入住，不能登记外出");
        }

        // 校验：该客户是否有未返回的外出记录
        boolean hasActiveOuting = count(new LambdaQueryWrapper<Outing>()
                .eq(Outing::getCustomerId, entity.getCustomerId())
                .eq(Outing::getStatus, "外出中")) > 0;
        if (hasActiveOuting) {
            throw new BusinessException("该客户当前外出中，不能重复登记外出");
        }

        entity.setOutingNo(NumberGenerator.next("O", prefix -> baseMapper.selectMaxByPrefix(prefix)));
        if (entity.getStatus() == null) {
            entity.setStatus("外出中");
        }
        return super.save(entity);
    }
}
