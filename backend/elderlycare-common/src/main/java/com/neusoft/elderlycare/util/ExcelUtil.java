package com.neusoft.elderlycare.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Excel 导出工具类 — 支持模板填充（只填数据，不写表头）
 */
public class ExcelUtil {

    /**
     * 基于模板导出 Excel — 读取模板表头确定字段顺序，只填入数据行
     *
     * @param response     HTTP 响应
     * @param templatePath 模板文件路径（classpath 下）
     * @param sheetName    工作表名称
     * @param headers      字段名 → 中文表头映射
     * @param dataList     数据列表
     */
    public static void exportWithTemplate(HttpServletResponse response, String templatePath,
                                           String sheetName, Map<String, String> headers,
                                           List<Map<String, Object>> dataList) throws IOException {
        setResponseHeader(response, sheetName);

        // 1. 读取模板表头顺序（中文名）
        List<String> templateHeaders = readTemplateHeaders(templatePath);

        // 2. 构建反向映射：中文表头 → 字段名
        Map<String, String> reverseMap = new LinkedHashMap<>();
        headers.forEach((fieldName, label) -> reverseMap.put(label, fieldName));

        // 3. 按模板表头顺序确定字段名列表
        List<String> fieldOrder = new ArrayList<>();
        for (String headerLabel : templateHeaders) {
            String fieldName = reverseMap.get(headerLabel);
            fieldOrder.add(fieldName != null ? fieldName : "");
        }

        // 4. 构建数据行（按模板字段顺序）
        List<List<Object>> rows = new ArrayList<>();
        for (Map<String, Object> item : dataList) {
            List<Object> row = new ArrayList<>();
            for (String field : fieldOrder) {
                row.add(field.isEmpty() ? "" : item.getOrDefault(field, ""));
            }
            rows.add(row);
        }

        // 5. 使用模板填充模式 — 只写数据，保留模板表头和格式
        try (InputStream templateStream = new ClassPathResource(templatePath).getInputStream()) {
            EasyExcel.write(response.getOutputStream())
                    .withTemplate(templateStream)
                    .sheet(sheetName)
                    .doFill(rows, FillConfig.builder().forceNewRow(Boolean.TRUE).build());
        }
    }

    /**
     * 读取模板第一行表头
     */
    private static List<String> readTemplateHeaders(String templatePath) throws IOException {
        try (InputStream is = new ClassPathResource(templatePath).getInputStream()) {
            List<Map<Integer, String>> result = EasyExcel.read(is)
                    .sheet()
                    .headRowNumber(1)
                    .doReadSync();
            if (result.isEmpty()) return Collections.emptyList();

            Map<Integer, String> firstRow = result.get(0);
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < firstRow.size(); i++) {
                headers.add(firstRow.getOrDefault(i, ""));
            }
            return headers;
        }
    }

    /**
     * 无模板导出（兜底方案）
     */
    public static void exportMap(HttpServletResponse response, String sheetName,
                                  Map<String, String> headers, List<Map<String, Object>> dataList) throws IOException {
        setResponseHeader(response, sheetName);

        List<String> headKeys = List.copyOf(headers.keySet());
        List<List<String>> headList = headKeys.stream()
                .map(Collections::singletonList)
                .toList();

        List<List<Object>> rows = dataList.stream()
                .map(row -> headKeys.stream()
                        .map(key -> row.getOrDefault(key, ""))
                        .toList())
                .toList();

        EasyExcel.write(response.getOutputStream())
                .head(headList)
                .sheet(sheetName)
                .doWrite(rows);
    }

    /**
     * 设置 Excel 导出响应头
     */
    private static void setResponseHeader(HttpServletResponse response, String sheetName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(sheetName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
    }
}
