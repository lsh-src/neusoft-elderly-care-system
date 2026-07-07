package com.neusoft.elderlycare.meal.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.elderlycare.common.BusinessException;
import com.neusoft.elderlycare.entity.Customer;
import com.neusoft.elderlycare.entity.Meal;
import com.neusoft.elderlycare.feign.CustomerFeignClient;
import com.neusoft.elderlycare.meal.mapper.MealMapper;
import com.neusoft.elderlycare.meal.service.MealService;
import com.neusoft.elderlycare.util.NumberGenerator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MealServiceImpl extends ServiceImpl<MealMapper, Meal> implements MealService {
    @Resource
    private MealMapper mealMapper;
    @Resource
    private CustomerFeignClient customerFeignClient;

    @Override
    public Page<Meal> getMealPage(Page<Meal> page, String keyword) {
        return mealMapper.selectMealPage(page, keyword);
    }

    @Override
    public boolean save(Meal entity) {
        // 校验客户是否存在
        if (entity.getCustomerId() != null) {
            Customer c = customerFeignClient.getById(entity.getCustomerId()).getData();
            if (c == null) {
                throw new BusinessException("客户不存在");
            }
        }
        entity.setMealNo(NumberGenerator.next("M", prefix -> baseMapper.selectMaxByPrefix(prefix)));
        return super.save(entity);
    }
}
