package com.neusoft.elderlycare.dashboard.service.impl;

import com.neusoft.elderlycare.entity.CheckIn;
import com.neusoft.elderlycare.entity.NursingRecord;
import com.neusoft.elderlycare.feign.*;
import com.neusoft.elderlycare.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {
    private final CustomerFeignClient customerFeignClient;
    private final BedFeignClient bedFeignClient;
    private final NursingFeignClient nursingFeignClient;
    private final ServicePurchaseFeignClient servicePurchaseFeignClient;
    private final CheckInFeignClient checkInFeignClient;

    @Override
    public Map<String, Object> stats() {
        Map<String, Object> data = new LinkedHashMap<>();

        // 通过 Feign 调用各服务获取统计数据
        long totalCustomers = Optional.ofNullable(customerFeignClient.count(null).getData()).orElse(0L);
        long checkedIn = Optional.ofNullable(customerFeignClient.count(1).getData()).orElse(0L);
        long freeBeds = 0L;
        Map<String, Long> bedStats = bedFeignClient.statusStats().getData();
        if (bedStats != null) {
            freeBeds = bedStats.getOrDefault("空闲", 0L);
        }
        long nursingRecords = Optional.ofNullable(nursingFeignClient.listAll().getData())
                .map(List::size).orElse(0);
        long servicePurchases = Optional.ofNullable(servicePurchaseFeignClient.count().getData()).orElse(0L);

        data.put("totalCustomers", totalCustomers);
        data.put("checkedInCustomers", checkedIn);
        data.put("freeBeds", freeBeds);
        data.put("nursingRecords", nursingRecords);
        data.put("servicePurchases", servicePurchases);
        data.put("bedStats", bedStats != null ? bedStats : Map.of());
        data.put("checkInTrend", checkInTrend());
        data.put("nursingStats", nursingStats());
        return data;
    }

    private List<Map<String, Object>> checkInTrend() {
        List<Map<String, Object>> rows = new ArrayList<>();
        LocalDate today = LocalDate.now();
        List<CheckIn> allCheckIns = Optional.ofNullable(checkInFeignClient.listAll().getData())
                .orElse(Collections.emptyList());

        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            long count = allCheckIns.stream()
                    .filter(ci -> day.equals(ci.getCheckInDate()))
                    .count();
            rows.add(Map.of("date", day.toString(), "count", count));
        }
        return rows;
    }

    private List<Map<String, Object>> nursingStats() {
        List<NursingRecord> records = Optional.ofNullable(nursingFeignClient.listAll().getData())
                .orElse(Collections.emptyList());
        Map<String, Long> grouped = new LinkedHashMap<>();
        for (NursingRecord record : records) {
            grouped.merge(record.getResult() == null ? "未填写" : record.getResult(), 1L, Long::sum);
        }
        return grouped.entrySet().stream()
                .map(entry -> Map.<String, Object>of("name", entry.getKey(), "value", entry.getValue()))
                .toList();
    }
}
