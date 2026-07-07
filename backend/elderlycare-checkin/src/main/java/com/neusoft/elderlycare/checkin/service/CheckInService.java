package com.neusoft.elderlycare.checkin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.CheckIn;

public interface CheckInService extends IService<CheckIn> {
    boolean register(CheckIn checkIn);
    // 自定义分页
    IPage<CheckIn> pageWithRelation(PageQuery query);
    // 原有注册方法
}
