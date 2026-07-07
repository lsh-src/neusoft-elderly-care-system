package com.neusoft.elderlycare.meal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.Meal;
import com.neusoft.elderlycare.meal.service.MealService;
import com.neusoft.elderlycare.util.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','NURSE','USER')")
@Tag(name = "膳食管理", description = "膳食信息 CRUD")
public class MealController {

    private final MealService mealService;

    // ===================== 分页接口 =====================
    @Operation(summary = "分页查询膳食", description = "根据条件分页查询膳食列表")
    @GetMapping("/page")
    public ApiResponse<Page<Meal>> page(PageQuery query) {
        Page<Meal> page = new Page<>(query.getCurrent(), query.getSize());
        Page<Meal> result = mealService.getMealPage(page, query.getKeyword());
        return ApiResponse.success(result);
    }

    // ===================== 导出 Excel（必须在 /{id} 之前） =====================
    private static final Map<String, String> FIELD_LABEL_MAP = Map.ofEntries(
            Map.entry("mealNo", "餐食编号"), Map.entry("customerName", "客户姓名"),
            Map.entry("breakfast", "早餐"), Map.entry("lunch", "午餐"),
            Map.entry("dinner", "晚餐"), Map.entry("specialNeed", "特殊需求"),
            Map.entry("mealDate", "日期"), Map.entry("mealImg", "膳食图片"),
            Map.entry("createTime", "创建时间")
    );

    @Operation(summary = "导出Excel", description = "导出膳食数据为Excel文件")
    @GetMapping("/export")
    public void export(PageQuery query, HttpServletResponse response) throws Exception {
        Page<Meal> page = new Page<>(1, 1000);
        List<Meal> list = mealService.getMealPage(page, query.getKeyword()).getRecords();
        if (list.isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":400,\"message\":\"没有可导出的数据\"}");
            return;
        }

        Map<String, String> headers = new LinkedHashMap<>(FIELD_LABEL_MAP);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Meal item : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (String key : headers.keySet()) {
                try {
                    PropertyDescriptor pd = new PropertyDescriptor(key, Meal.class);
                    Method getter = pd.getReadMethod();
                    Object value = getter.invoke(item);
                    if (value instanceof LocalDateTime ldt) {
                        row.put(key, ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    } else {
                        row.put(key, value != null ? value : "");
                    }
                } catch (Exception e) {
                    row.put(key, "");
                }
            }
            dataList.add(row);
        }

        try {
            ExcelUtil.exportWithTemplate(response, "excel_model/MealManagement.xlsx", "膳食管理", headers, dataList);
        } catch (Exception e) {
            log.warn("模板导出失败，降级为无模板导出: {}", e.getMessage());
            ExcelUtil.exportMap(response, "膳食管理", headers, dataList);
        }
    }

    // ===================== 新增 =====================
    @Operation(summary = "新增膳食", description = "新增一条膳食记录")
    @PostMapping
    public ApiResponse<Boolean> add(@RequestBody Meal meal) {
        return ApiResponse.success(mealService.save(meal));
    }

    // ===================== 修改 =====================
    @Operation(summary = "修改膳食", description = "根据ID修改膳食信息")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@Parameter(description = "膳食ID", required = true, example = "1") @PathVariable Long id, @RequestBody Meal meal) {
        meal.setId(id);
        return ApiResponse.success(mealService.updateById(meal));
    }

    // ===================== 删除 =====================
    @Operation(summary = "删除膳食", description = "根据ID删除膳食记录")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@Parameter(description = "膳食ID", required = true, example = "1") @PathVariable Long id) {
        return ApiResponse.success(mealService.removeById(id));
    }

    // ===================== 详情 =====================
    @Operation(summary = "查询膳食详情", description = "根据ID查询膳食详情")
    @GetMapping("/{id}")
    public ApiResponse<Meal> getById(@Parameter(description = "膳食ID", required = true, example = "1") @PathVariable Long id) {
        return ApiResponse.success(mealService.getById(id));
    }
}