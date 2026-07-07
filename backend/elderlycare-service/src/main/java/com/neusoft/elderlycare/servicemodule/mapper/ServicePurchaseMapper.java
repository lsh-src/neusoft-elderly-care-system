package com.neusoft.elderlycare.servicemodule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.elderlycare.entity.ServicePurchase;
import org.apache.ibatis.annotations.Param;

public interface ServicePurchaseMapper extends BaseMapper<ServicePurchase> {
    // 方法名 = XML id，参数严格匹配
    IPage<ServicePurchase> selectServicePurchasePage(
            Page<ServicePurchase> page,
            @Param("keyword") String keyword
    );
}
