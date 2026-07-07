package com.neusoft.elderlycare.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.util.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 通用泛型CRUD控制器
 * T = 实体类（完全通用，不写死任何实体）
 */
@Slf4j
public abstract class BaseCrudController<T> {
    // 获取通用Service
    protected abstract IService<T> service();

    // 查询条件（子类可重写）
    protected QueryWrapper<T> wrapper(PageQuery query) {
        return new QueryWrapper<>();
    }

    // ===================== 核心修复：泛型通用分页 =====================
    @Operation(summary = "分页查询", description = "根据条件分页查询列表")
    @ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "查询成功",
            content = @Content(schema = @Schema(implementation = com.neusoft.elderlycare.common.ApiResponse.class)))})
    @GetMapping("/page")
    public com.neusoft.elderlycare.common.ApiResponse<IPage<T>> page(
            @Parameter(description = "分页查询参数") PageQuery query) {
        Page<T> page = new Page<>(query.getCurrent(), query.getSize());
        return com.neusoft.elderlycare.common.ApiResponse.success(service().page(page, wrapper(query)));
    }

    // ===================== 导出 Excel（必须在 /{id} 之前） =====================
    private static final Map<String, String> TEMPLATE_MAP = Map.ofEntries(
            Map.entry("users", "excel_model/UserManagement.xlsx"),
            Map.entry("customers", "excel_model/CustomerManagement.xlsx"),
            Map.entry("beds", "excel_model/BedManagement.xlsx"),
            Map.entry("check-ins", "excel_model/CheckInRegistration.xlsx"),
            Map.entry("check-outs", "excel_model/CheckOutRegistration.xlsx"),
            Map.entry("outings", "excel_model/OutingRegistration.xlsx"),
            Map.entry("meals", "excel_model/MealManagement.xlsx"),
            Map.entry("service-relations", "excel_model/ServiceAssignment.xlsx"),
            Map.entry("care-services", "excel_model/ServiceItem.xlsx"),
            Map.entry("service-purchases", "excel_model/ServicePurchase.xlsx"),
            Map.entry("nursing-levels", "excel_model/NursingLevel.xlsx"),
            Map.entry("nursing-items", "excel_model/NursingItem.xlsx"),
            Map.entry("nursing-records", "excel_model/NursingRecord.xlsx"),
            Map.entry("nurse-areas", "excel_model/ResponsibleArea.xlsx")
    );

    /** 字段名 → 中文表头映射（所有模块共用） */
    private static final Map<String, String> FIELD_LABEL_MAP = Map.ofEntries(
            // 用户管理
            Map.entry("phone", "手机号"), Map.entry("name", "姓名"), Map.entry("age", "年龄"),
            Map.entry("gender", "性别"), Map.entry("role", "角色"), Map.entry("enabled", "状态"),
            // 客户管理
            Map.entry("customerNo", "客户编号"), Map.entry("emergencyContact", "紧急联系人"),
            Map.entry("checkInDate", "入住日期"), Map.entry("healthStatus", "健康状态"),
            Map.entry("checkedIn", "是否入住"),
            // 床位管理
            Map.entry("roomNo", "房间编号"), Map.entry("bedNo", "床位编号"),
            Map.entry("status", "状态"), Map.entry("customerName", "客户姓名"),
            // 入住登记
            Map.entry("registerNo", "登记编号"), Map.entry("contractMonths", "合同期限(月)"),
            Map.entry("deposit", "押金"), Map.entry("operator", "经办人"),
            // 退住登记
            Map.entry("checkoutNo", "退住编号"), Map.entry("checkoutDate", "退住日期"),
            Map.entry("reason", "原因"),
            // 外出登记
            Map.entry("outingNo", "外出编号"), Map.entry("outTime", "外出时间"),
            Map.entry("expectedReturnTime", "预计返回时间"), Map.entry("actualReturnTime", "实际返回时间"),
            Map.entry("remark", "备注"),
            // 膳食管理
            Map.entry("mealNo", "餐食编号"), Map.entry("breakfast", "早餐"),
            Map.entry("lunch", "午餐"), Map.entry("dinner", "晚餐"),
            Map.entry("specialNeed", "特殊需求"), Map.entry("mealDate", "日期"),
            Map.entry("mealImg", "膳食图片"),
            // 服务管理
            Map.entry("managerName", "健康管家"), Map.entry("serviceName", "服务名称"),
            Map.entry("price", "价格"), Map.entry("content", "内容"), Map.entry("period", "周期"),
            Map.entry("purchaseDate", "购买日期"),
            // 护理管理
            Map.entry("levelName", "级别名称"), Map.entry("description", "说明"),
            Map.entry("fee", "费用"), Map.entry("itemName", "项目名称"),
            Map.entry("frequency", "执行频率"), Map.entry("recordNo", "记录编号"),
            Map.entry("nurseName", "护理人员"), Map.entry("nursingTime", "护理时间"),
            Map.entry("result", "结果"),
            // 区域
            Map.entry("areaName", "区域名称"),
            // 通用
            Map.entry("id", "ID"), Map.entry("createTime", "创建时间"), Map.entry("updateTime", "更新时间"),
            Map.entry("password", "密码")
    );

    @Operation(summary = "导出Excel", description = "导出当前查询条件下的所有数据为Excel文件")
    @GetMapping("/export")
    public void export(PageQuery query, HttpServletResponse response, HttpServletRequest request) throws Exception {
        List<T> list = service().list(wrapper(query));
        if (list.isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":400,\"message\":\"没有可导出的数据\"}");
            return;
        }

        // 动态提取表头和数据
        Map<String, String> headers = new LinkedHashMap<>(); // key=字段名, value=中文表头
        List<Map<String, Object>> dataList = new ArrayList<>();

        Class<?> clazz = list.get(0).getClass();
        List<Field> allFields = getAllFields(clazz);

        // 跳过不应导出的字段
        Set<String> skipFields = Set.of("serialVersionUID", "deleted", "password", "embedding");
        for (Field field : allFields) {
            String name = field.getName();
            if (skipFields.contains(name) || name.contains("$")) continue;
            String label = FIELD_LABEL_MAP.getOrDefault(name, name);
            headers.put(name, label);
        }

        for (T item : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (String key : headers.keySet()) {
                try {
                    PropertyDescriptor pd = new PropertyDescriptor(key, clazz);
                    Method getter = pd.getReadMethod();
                    Object value = getter.invoke(item);
                    if (value instanceof LocalDateTime ldt) {
                        row.put(key, ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    } else if (value instanceof Integer i && key.equals("enabled")) {
                        row.put(key, i == 1 ? "正常" : "禁用");
                    } else if (value instanceof Integer i && key.equals("checkedIn")) {
                        row.put(key, i == 1 ? "已入住" : "未入住");
                    } else {
                        row.put(key, value != null ? value : "");
                    }
                } catch (Exception e) {
                    row.put(key, "");
                }
            }
            dataList.add(row);
        }

        String sheetName = getExportSheetName();
        String resource = getExportResource();
        if (resource.isEmpty()) {
            String uri = request.getRequestURI();
            String[] parts = uri.split("/");
            if (parts.length >= 3) {
                resource = parts[parts.length - 2];
            }
        }
        String templatePath = TEMPLATE_MAP.get(resource);

        if (templatePath != null) {
            try {
                ExcelUtil.exportWithTemplate(response, templatePath, sheetName, headers, dataList);
            } catch (Exception e) {
                log.warn("模板导出失败，降级为无模板导出: {}", e.getMessage());
                ExcelUtil.exportMap(response, sheetName, headers, dataList);
            }
        } else {
            ExcelUtil.exportMap(response, sheetName, headers, dataList);
        }
    }

    /** 子类可重写，自定义导出工作表名称 */
    protected String getExportSheetName() {
        return "数据导出";
    }

    /** 获取模板路径，子类可重写 */
    protected String getExportTemplatePath() {
        return TEMPLATE_MAP.get(getExportResource());
    }

    /** 获取资源名，用于匹配模板。子类可重写覆盖 */
    protected String getExportResource() {
        return "";
    }

    /** 递归获取所有字段（含父类） */
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    @Operation(summary = "查询详情", description = "根据ID查询详情")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "数据不存在")
    })
    @GetMapping("/{id}")
    public com.neusoft.elderlycare.common.ApiResponse<T> detail(
            @Parameter(description = "数据ID", required = true, example = "1") @PathVariable Long id) {
        T entity = service().getById(id);
        if (entity == null) {
            return new com.neusoft.elderlycare.common.ApiResponse<>(404, "数据不存在", null);
        }
        return com.neusoft.elderlycare.common.ApiResponse.success(entity);
    }

    @Operation(summary = "新增", description = "新增一条数据")
    @ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "新增成功")})
    @PostMapping
    public com.neusoft.elderlycare.common.ApiResponse<Boolean> create(
            @Parameter(description = "新增数据", required = true) @RequestBody T body) {
        return com.neusoft.elderlycare.common.ApiResponse.success(service().save(body));
    }

    @Operation(summary = "修改", description = "根据ID修改数据")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "修改成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "数据不存在")
    })
    @PutMapping("/{id}")
    public com.neusoft.elderlycare.common.ApiResponse<Boolean> update(
            @Parameter(description = "数据ID", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "修改数据", required = true) @RequestBody T body) {
        setId(body, id);
        return com.neusoft.elderlycare.common.ApiResponse.success(service().updateById(body));
    }

    @Operation(summary = "删除", description = "根据ID删除数据")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "数据不存在")
    })
    @DeleteMapping("/{id}")
    public com.neusoft.elderlycare.common.ApiResponse<Boolean> delete(
            @Parameter(description = "数据ID", required = true, example = "1") @PathVariable Long id) {
        boolean removed = service().removeById(id);
        if (!removed) {
            return new com.neusoft.elderlycare.common.ApiResponse<>(404, "数据不存在", null);
        }
        return com.neusoft.elderlycare.common.ApiResponse.success(true);
    }

    // 反射设置ID
    private void setId(T body, Long id) {
        try {
            body.getClass().getMethod("setId", Long.class).invoke(body, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("实体必须包含 setId(Long) 方法");
        }
    }
}
