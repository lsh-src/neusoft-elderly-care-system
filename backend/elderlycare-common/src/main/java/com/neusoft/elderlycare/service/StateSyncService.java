package com.neusoft.elderlycare.service;

import com.neusoft.elderlycare.entity.Bed;
import com.neusoft.elderlycare.entity.Customer;
import com.neusoft.elderlycare.feign.BedFeignClient;
import com.neusoft.elderlycare.feign.CustomerFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 跨服务状态同步 — 带自动重试
 *
 * 用于 checkin/checkout 模块在本地事务提交后，
 * 通过 Feign 调用更新 customer 和 bed 的状态。
 * 失败时自动重试 3 次（指数退避），全部失败后走 recover 兜底。
 */
@Service
@ConditionalOnBean({CustomerFeignClient.class, BedFeignClient.class})
@RequiredArgsConstructor
@Slf4j
public class StateSyncService {

    private final CustomerFeignClient customerFeignClient;
    private final BedFeignClient bedFeignClient;

    /**
     * 标记客户已入住（带重试）
     */
    @Retryable(
        retryFor = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void markCustomerCheckedIn(Long customerId, LocalDate checkInDate) {
        Customer c = customerFeignClient.getById(customerId).getData();
        if (c == null) {
            log.warn("[StateSync] 客户不存在: customerId={}", customerId);
            return;
        }
        c.setCheckedIn(1);
        if (checkInDate != null) {
            c.setCheckInDate(checkInDate);
        }
        customerFeignClient.updateById(c.getId(), c);
        log.info("[StateSync] 客户已入住: customerId={}, name={}", customerId, c.getName());
    }

    @Recover
    public void recoverMarkCustomerCheckedIn(Exception e, Long customerId, LocalDate checkInDate) {
        log.error("[StateSync] 客户入住状态同步最终失败（已重试3次）: customerId={}, error={}",
                customerId, e.getMessage());
    }

    /**
     * 标记客户已退住（带重试）
     */
    @Retryable(
        retryFor = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void markCustomerCheckedOut(Long customerId) {
        Customer c = customerFeignClient.getById(customerId).getData();
        if (c == null) {
            log.warn("[StateSync] 客户不存在: customerId={}", customerId);
            return;
        }
        c.setCheckedIn(0);
        customerFeignClient.updateById(c.getId(), c);
        log.info("[StateSync] 客户已退住: customerId={}, name={}", customerId, c.getName());
    }

    @Recover
    public void recoverMarkCustomerCheckedOut(Exception e, Long customerId) {
        log.error("[StateSync] 客户退住状态同步最终失败（已重试3次）: customerId={}, error={}",
                customerId, e.getMessage());
    }

    /**
     * 分配床位给客户（带重试）
     */
    @Retryable(
        retryFor = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void assignBed(Long bedId, Long customerId) {
        Bed bed = bedFeignClient.getById(bedId).getData();
        if (bed == null) {
            log.warn("[StateSync] 床位不存在: bedId={}", bedId);
            return;
        }
        bed.setCustomerId(customerId);
        bed.setStatus("已入住");
        bedFeignClient.updateById(bed.getId(), bed);
        log.info("[StateSync] 床位已分配: bedId={}, customerId={}", bedId, customerId);
    }

    @Recover
    public void recoverAssignBed(Exception e, Long bedId, Long customerId) {
        log.error("[StateSync] 床位分配同步最终失败（已重试3次）: bedId={}, customerId={}, error={}",
                bedId, customerId, e.getMessage());
    }

    /**
     * 释放床位（带重试）
     */
    @Retryable(
        retryFor = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void releaseBed(Long bedId) {
        Bed bed = bedFeignClient.getById(bedId).getData();
        if (bed == null) {
            log.warn("[StateSync] 床位不存在: bedId={}", bedId);
            return;
        }
        bed.setCustomerId(null);
        bed.setStatus("空闲");
        bedFeignClient.updateById(bed.getId(), bed);
        log.info("[StateSync] 床位已释放: bedId={}", bedId);
    }

    @Recover
    public void recoverReleaseBed(Exception e, Long bedId) {
        log.error("[StateSync] 床位释放同步最终失败（已重试3次）: bedId={}, error={}",
                bedId, e.getMessage());
    }

    /**
     * 修复客户残留入住状态（带重试）
     */
    @Retryable(
        retryFor = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void fixCustomerResidualState(Long customerId) {
        Customer c = customerFeignClient.getById(customerId).getData();
        if (c != null && c.getCheckedIn() != null && c.getCheckedIn() == 1) {
            c.setCheckedIn(0);
            customerFeignClient.updateById(c.getId(), c);
            log.info("[StateSync] 修复客户残留状态: customerId={}", customerId);
        }
    }

    @Recover
    public void recoverFixCustomerResidualState(Exception e, Long customerId) {
        log.error("[StateSync] 修复客户残留状态最终失败: customerId={}, error={}",
                customerId, e.getMessage());
    }

    /**
     * 修复床位残留状态（带重试）
     */
    @Retryable(
        retryFor = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void fixBedResidualState(Long bedId) {
        Bed bed = bedFeignClient.getById(bedId).getData();
        if (bed != null && !"空闲".equals(bed.getStatus())) {
            bed.setCustomerId(null);
            bed.setStatus("空闲");
            bedFeignClient.updateById(bed.getId(), bed);
            log.info("[StateSync] 修复床位残留状态: bedId={}", bedId);
        }
    }

    @Recover
    public void recoverFixBedResidualState(Exception e, Long bedId) {
        log.error("[StateSync] 修复床位残留状态最终失败: bedId={}, error={}",
                bedId, e.getMessage());
    }
}
