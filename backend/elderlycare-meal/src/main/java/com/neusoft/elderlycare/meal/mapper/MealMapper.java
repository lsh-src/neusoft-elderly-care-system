package com.neusoft.elderlycare.meal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.elderlycare.entity.Meal;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface MealMapper extends BaseMapper<Meal> {
    // 🔥 自定义联表分页查询（meal + customer 联查）
    Page<Meal> selectMealPage(Page<Meal> page, @Param("keyword") String keyword);
    @Select("SELECT meal_no FROM meal " +
            "WHERE meal_no LIKE CONCAT(#{prefix}, '%') " +
            "ORDER BY meal_no DESC LIMIT 1")
    String selectMaxByPrefix(@Param("prefix") String prefix);
}