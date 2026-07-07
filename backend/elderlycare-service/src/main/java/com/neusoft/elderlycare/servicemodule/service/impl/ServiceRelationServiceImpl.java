package com.neusoft.elderlycare.servicemodule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.elderlycare.common.BusinessException;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.Customer;
import com.neusoft.elderlycare.entity.ServiceRelation;
import com.neusoft.elderlycare.feign.CustomerFeignClient;
import com.neusoft.elderlycare.servicemodule.mapper.ServiceRelationMapper;
import com.neusoft.elderlycare.servicemodule.service.ServiceRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceRelationServiceImpl extends ServiceImpl<ServiceRelationMapper, ServiceRelation> implements ServiceRelationService {
    private final ServiceRelationMapper serviceRelationMapper;
    private final CustomerFeignClient customerFeignClient;

    @Override
    public IPage<ServiceRelation> pageWithName(PageQuery query) {
        Page<ServiceRelation> page = new Page<>(query.getCurrent(), query.getSize());
        return serviceRelationMapper.selectServiceRelationPage(page, query.getKeyword());
    }

    @Override
    public boolean save(ServiceRelation entity) {
        // 校验客户是否存在
        if (entity.getCustomerId() != null) {
            Customer c = customerFeignClient.getById(entity.getCustomerId()).getData();
            if (c == null) {
                throw new BusinessException("客户不存在");
            }
        }
        // 校验：同一客户不能重复分配同一管家
        if (entity.getCustomerId() != null && entity.getManagerId() != null) {
            boolean exists = count(new LambdaQueryWrapper<ServiceRelation>()
                    .eq(ServiceRelation::getCustomerId, entity.getCustomerId())
                    .eq(ServiceRelation::getManagerId, entity.getManagerId())) > 0;
            if (exists) {
                throw new BusinessException("该客户已分配此管家，不能重复添加");
            }
        }
        return super.save(entity);
    }
}
