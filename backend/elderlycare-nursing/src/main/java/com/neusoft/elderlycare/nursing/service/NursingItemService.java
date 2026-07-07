package com.neusoft.elderlycare.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.NursingItem;

public interface NursingItemService extends IService<NursingItem> {
    IPage<NursingItem> pageWithName(PageQuery query);
}
