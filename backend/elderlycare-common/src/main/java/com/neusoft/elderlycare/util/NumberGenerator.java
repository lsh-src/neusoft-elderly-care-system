package com.neusoft.elderlycare.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 通用编号生成器
 * 格式：前缀 + 日期 + 三位序号
 * 例如：C20260605-001、CO20260605-001、M20260605-001
 */
public class NumberGenerator {

    private static final Logger log = LoggerFactory.getLogger(NumberGenerator.class);

    @FunctionalInterface
    public interface MaxNoQuery {
        String selectMaxNo(String prefix);
    }

    public static String next(String prefix, MaxNoQuery query) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";

        String maxNo = query.selectMaxNo(fullPrefix);
        log.debug("prefix={}, maxNo={}", fullPrefix, maxNo);

        int seq = 1;
        if (maxNo != null && maxNo.contains("-")) {
            try {
                String lastPart = maxNo.substring(maxNo.lastIndexOf("-") + 1);
                seq = Integer.parseInt(lastPart) + 1;
            } catch (NumberFormatException e) {
                log.warn("编号解析失败: {}", maxNo);
            }
        }

        String result = fullPrefix + String.format("%03d", seq);
        log.debug("生成编号: {}", result);
        return result;
    }
}
