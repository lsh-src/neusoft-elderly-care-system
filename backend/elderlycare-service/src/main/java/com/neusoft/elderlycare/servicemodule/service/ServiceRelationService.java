package com.neusoft.elderlycare.servicemodule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.ServiceRelation;

public interface ServiceRelationService extends IService<ServiceRelation> {
    IPage<ServiceRelation> pageWithName(PageQuery query);
}
