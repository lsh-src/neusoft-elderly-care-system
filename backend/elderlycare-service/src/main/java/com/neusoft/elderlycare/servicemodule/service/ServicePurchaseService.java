package com.neusoft.elderlycare.servicemodule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.ServicePurchase;

public interface ServicePurchaseService extends IService<ServicePurchase> {
    IPage<ServicePurchase> pageWithName(PageQuery query);
}
