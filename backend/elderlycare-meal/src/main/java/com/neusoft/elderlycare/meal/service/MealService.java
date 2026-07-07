package com.neusoft.elderlycare.meal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.entity.Meal;

public interface MealService extends IService<Meal> {
    Page<Meal> getMealPage(Page<Meal> page, String keyword);
}
