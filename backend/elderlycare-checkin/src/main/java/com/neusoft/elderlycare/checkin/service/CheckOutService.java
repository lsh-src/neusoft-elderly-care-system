package com.neusoft.elderlycare.checkin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.CheckOut;

public interface CheckOutService extends IService<CheckOut> {
    boolean checkout(CheckOut checkOut);
    IPage<CheckOut> pageWithCustomer(PageQuery query);
}
