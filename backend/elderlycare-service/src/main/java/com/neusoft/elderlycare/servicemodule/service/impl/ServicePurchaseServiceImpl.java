package com.neusoft.elderlycare.servicemodule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.elderlycare.common.BusinessException;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.CareService;
import com.neusoft.elderlycare.entity.Customer;
import com.neusoft.elderlycare.entity.ServicePurchase;
import com.neusoft.elderlycare.feign.CustomerFeignClient;
import com.neusoft.elderlycare.servicemodule.mapper.ServicePurchaseMapper;
import com.neusoft.elderlycare.mq.BusinessEvent;
import com.neusoft.elderlycare.mq.MqConstants;
import com.neusoft.elderlycare.servicemodule.service.CareServiceService;
import com.neusoft.elderlycare.servicemodule.service.ServicePurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicePurchaseServiceImpl extends ServiceImpl<ServicePurchaseMapper, ServicePurchase> implements ServicePurchaseService {
    private final ServicePurchaseMapper purchaseMapper;
    private final RabbitTemplate rabbitTemplate;
    private final CustomerFeignClient customerFeignClient;
    private final CareServiceService careServiceService;

    @Override
    public IPage<ServicePurchase> pageWithName(PageQuery query) {
        Page<ServicePurchase> page = new Page<>(query.getCurrent(), query.getSize());
        return purchaseMapper.selectServicePurchasePage(page, query.getKeyword());
    }

    @Override
    public boolean save(ServicePurchase entity) {
        // 校验客户是否存在
        if (entity.getCustomerId() != null) {
            Customer c = customerFeignClient.getById(entity.getCustomerId()).getData();
            if (c == null) {
                throw new BusinessException("客户不存在");
            }
        }
        // 校验服务项目是否存在
        if (entity.getServiceId() != null) {
            CareService service = careServiceService.getById(entity.getServiceId());
            if (service == null) {
                throw new BusinessException("服务项目不存在");
            }
        }
        // 校验：同一客户不能重复购买同一服务（状态为有效的）
        if (entity.getCustomerId() != null && entity.getServiceId() != null) {
            boolean exists = count(new LambdaQueryWrapper<ServicePurchase>()
                    .eq(ServicePurchase::getCustomerId, entity.getCustomerId())
                    .eq(ServicePurchase::getServiceId, entity.getServiceId())
                    .eq(ServicePurchase::getStatus, "有效")) > 0;
            if (exists) {
                throw new BusinessException("该客户已购买此服务且尚未过期，不能重复购买");
            }
        }
        boolean saved = super.save(entity);
        if (saved) {
            try {
                BusinessEvent<ServicePurchase> event = BusinessEvent.of(
                        "SERVICE_PURCHASED", entity, "elderlycare-service");
                rabbitTemplate.convertAndSend(MqConstants.EXCHANGE_BUSINESS,
                        MqConstants.KEY_SERVICE_PURCHASED, event);
                log.info("[MQ] 服务购买事件已发送: service={}", entity.getServiceName());
            } catch (Exception e) {
                log.error("[MQ] 服务购买事件发送失败: {}", e.getMessage());
            }
        }
        return saved;
    }
}
