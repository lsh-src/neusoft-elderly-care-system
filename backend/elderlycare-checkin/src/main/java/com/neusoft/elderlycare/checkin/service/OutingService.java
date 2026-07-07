package com.neusoft.elderlycare.checkin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.Outing;

public interface OutingService extends IService<Outing> {
    boolean returnBack(Long id);
    IPage<Outing> pageWithCustomer(PageQuery query);
}
