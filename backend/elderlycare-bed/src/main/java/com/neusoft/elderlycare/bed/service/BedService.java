package com.neusoft.elderlycare.bed.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.Bed;

import java.util.Map;

public interface BedService extends IService<Bed> {
    void assign(Long bedId, Long customerId);
    void release(Long bedId);
    Map<String, Long> statusStats();
    IPage<Bed> pageWithCustomer(PageQuery query);
}
