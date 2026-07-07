package com.neusoft.elderlycare.bed.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.elderlycare.common.BusinessException;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.Bed;
import com.neusoft.elderlycare.bed.mapper.BedMapper;
import com.neusoft.elderlycare.bed.service.BedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BedServiceImpl extends ServiceImpl<BedMapper, Bed> implements BedService {

    private final BedMapper bedMapper;

    @Override
    public boolean save(Bed entity) {
        bedMapper.physicallyRemoveConflict(entity.getRoomNo(), entity.getBedNo());
        if (entity.getCustomerId() != null) {
            entity.setStatus("已入住");
        } else if (entity.getStatus() == null) {
            entity.setStatus("空闲");
        }
        return super.save(entity);
    }

    @Override
    public boolean updateById(Bed entity) {
        if (entity.getCustomerId() != null) {
            entity.setStatus("已入住");
        } else if ("已入住".equals(entity.getStatus())) {
            entity.setStatus("空闲");
        }
        return super.updateById(entity);
    }

    @Transactional
    public void assign(Long bedId, Long customerId) {
        Bed bed = getById(bedId);
        if (bed == null) throw new BusinessException("床位不存在");
        if (!"空闲".equals(bed.getStatus())) throw new BusinessException("床位当前不可分配");
        bed.setCustomerId(customerId);
        bed.setStatus("已入住");
        super.updateById(bed);
    }

    @Transactional
    public void release(Long bedId) {
        Bed bed = getById(bedId);
        if (bed == null) throw new BusinessException("床位不存在");
        bed.setCustomerId(null);
        bed.setStatus("空闲");
        super.updateById(bed);
    }

    @Override
    public IPage<Bed> pageWithCustomer(PageQuery query) {
        Page<Bed> page = new Page<>(query.getCurrent(), query.getSize());
        return bedMapper.selectBedPage(page, query.getKeyword());
    }

    @Override
    public Map<String, Long> statusStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("空闲", count(new LambdaQueryWrapper<Bed>().eq(Bed::getStatus, "空闲")));
        stats.put("已入住", count(new LambdaQueryWrapper<Bed>().eq(Bed::getStatus, "已入住")));
        stats.put("维修中", count(new LambdaQueryWrapper<Bed>().eq(Bed::getStatus, "维修中")));
        return stats;
    }
}
