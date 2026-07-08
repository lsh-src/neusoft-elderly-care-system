package com.neusoft.elderlycare.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NumberGenerator 编号生成器测试")
class NumberGeneratorTest {

    private String today() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    @Test
    @DisplayName("数据库无历史编号时应生成 -001")
    void firstNumber() {
        String result = NumberGenerator.next("C", prefix -> null);

        assertEquals("C" + today() + "-001", result);
    }

    @Test
    @DisplayName("数据库有历史编号时应递增")
    void incrementFromExisting() {
        String existing = "C" + today() + "-005";
        String result = NumberGenerator.next("C", prefix -> existing);

        assertEquals("C" + today() + "-006", result);
    }

    @Test
    @DisplayName("不同前缀应生成不同编号序列")
    void differentPrefixes() {
        String c = NumberGenerator.next("C", p -> null);
        String co = NumberGenerator.next("CO", p -> null);
        String m = NumberGenerator.next("M", p -> null);

        assertTrue(c.startsWith("C"));
        assertTrue(co.startsWith("CO"));
        assertTrue(m.startsWith("M"));
        assertNotEquals(c, co);
        assertNotEquals(co, m);
    }

    @Test
    @DisplayName("序号应三位补零")
    void zeroPadded() {
        String result = NumberGenerator.next("C", prefix -> null);

        assertTrue(result.endsWith("-001"), "应以 -001 结尾: " + result);
    }

    @Test
    @DisplayName("序号 9 应生成 010")
    void incrementSingleDigit() {
        String existing = "C" + today() + "-009";
        String result = NumberGenerator.next("C", prefix -> existing);

        assertEquals("C" + today() + "-010", result);
    }

    @Test
    @DisplayName("序号 99 应生成 100")
    void incrementDoubleDigit() {
        String existing = "C" + today() + "-099";
        String result = NumberGenerator.next("C", prefix -> existing);

        assertEquals("C" + today() + "-100", result);
    }

    @Test
    @DisplayName("历史编号格式异常时应降级为 001")
    void malformedHistory() {
        String result = NumberGenerator.next("C", prefix -> "INVALID_FORMAT");

        assertEquals("C" + today() + "-001", result);
    }

    @Test
    @DisplayName("历史编号无横线时应降级为 001")
    void noDashInHistory() {
        String result = NumberGenerator.next("C", prefix -> "C20260708001");

        assertEquals("C" + today() + "-001", result);
    }

    @Test
    @DisplayName("prefix 参数应正确拼接到结果中")
    void prefixCorrectness() {
        String r1 = NumberGenerator.next("RZ", p -> null);
        String r2 = NumberGenerator.next("CHECKIN", p -> null);

        assertTrue(r1.startsWith("RZ" + today()));
        assertTrue(r2.startsWith("CHECKIN" + today()));
    }
}
