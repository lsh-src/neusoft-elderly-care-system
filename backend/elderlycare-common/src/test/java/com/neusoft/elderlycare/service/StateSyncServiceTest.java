package com.neusoft.elderlycare.service;

import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.entity.Bed;
import com.neusoft.elderlycare.entity.Customer;
import com.neusoft.elderlycare.feign.BedFeignClient;
import com.neusoft.elderlycare.feign.CustomerFeignClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StateSyncService 跨服务状态同步测试")
class StateSyncServiceTest {

    @Mock
    private CustomerFeignClient customerFeignClient;

    @Mock
    private BedFeignClient bedFeignClient;

    @InjectMocks
    private StateSyncService stateSyncService;

    // ==================== markCustomerCheckedIn ====================

    @Test
    @DisplayName("markCustomerCheckedIn 应将客户 checkedIn 设为 1")
    void markCustomerCheckedIn_success() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("张三");
        customer.setCheckedIn(0);

        when(customerFeignClient.getById(1L)).thenReturn(ApiResponse.success(customer));
        when(customerFeignClient.updateById(eq(1L), any(Customer.class))).thenReturn(ApiResponse.success(true));

        stateSyncService.markCustomerCheckedIn(1L, LocalDate.of(2026, 7, 8));

        assertEquals(1, customer.getCheckedIn());
        assertEquals(LocalDate.of(2026, 7, 8), customer.getCheckInDate());
        verify(customerFeignClient).updateById(eq(1L), any(Customer.class));
    }

    @Test
    @DisplayName("markCustomerCheckedIn 客户不存在时应跳过")
    void markCustomerCheckedIn_customerNotFound() {
        when(customerFeignClient.getById(999L)).thenReturn(ApiResponse.success(null));

        assertDoesNotThrow(() -> stateSyncService.markCustomerCheckedIn(999L, null));
        verify(customerFeignClient, never()).updateById(any(), any());
    }

    @Test
    @DisplayName("markCustomerCheckedIn checkInDate 为 null 时不应设置日期")
    void markCustomerCheckedIn_noDate() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCheckedIn(0);

        when(customerFeignClient.getById(1L)).thenReturn(ApiResponse.success(customer));
        when(customerFeignClient.updateById(eq(1L), any())).thenReturn(ApiResponse.success(true));

        stateSyncService.markCustomerCheckedIn(1L, null);

        assertEquals(1, customer.getCheckedIn());
        assertNull(customer.getCheckInDate());
    }

    // ==================== markCustomerCheckedOut ====================

    @Test
    @DisplayName("markCustomerCheckedOut 应将客户 checkedIn 设为 0")
    void markCustomerCheckedOut_success() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCheckedIn(1);

        when(customerFeignClient.getById(1L)).thenReturn(ApiResponse.success(customer));
        when(customerFeignClient.updateById(eq(1L), any())).thenReturn(ApiResponse.success(true));

        stateSyncService.markCustomerCheckedOut(1L);

        assertEquals(0, customer.getCheckedIn());
        verify(customerFeignClient).updateById(eq(1L), any());
    }

    // ==================== assignBed ====================

    @Test
    @DisplayName("assignBed 应设置床位状态为已入住并关联客户")
    void assignBed_success() {
        Bed bed = new Bed();
        bed.setId(10L);
        bed.setStatus("空闲");

        when(bedFeignClient.getById(10L)).thenReturn(ApiResponse.success(bed));
        when(bedFeignClient.updateById(eq(10L), any())).thenReturn(ApiResponse.success(true));

        stateSyncService.assignBed(10L, 1L);

        assertEquals("已入住", bed.getStatus());
        assertEquals(1L, bed.getCustomerId());
        verify(bedFeignClient).updateById(eq(10L), any());
    }

    @Test
    @DisplayName("assignBed 床位不存在时应跳过")
    void assignBed_bedNotFound() {
        when(bedFeignClient.getById(999L)).thenReturn(ApiResponse.success(null));

        assertDoesNotThrow(() -> stateSyncService.assignBed(999L, 1L));
        verify(bedFeignClient, never()).updateById(any(), any());
    }

    // ==================== releaseBed ====================

    @Test
    @DisplayName("releaseBed 应设置床位状态为空闲并清除客户关联")
    void releaseBed_success() {
        Bed bed = new Bed();
        bed.setId(10L);
        bed.setStatus("已入住");
        bed.setCustomerId(1L);

        when(bedFeignClient.getById(10L)).thenReturn(ApiResponse.success(bed));
        when(bedFeignClient.updateById(eq(10L), any())).thenReturn(ApiResponse.success(true));

        stateSyncService.releaseBed(10L);

        assertEquals("空闲", bed.getStatus());
        assertNull(bed.getCustomerId());
    }

    // ==================== fixCustomerResidualState ====================

    @Test
    @DisplayName("fixCustomerResidualState 应修复残留的入住状态")
    void fixCustomerResidualState_success() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCheckedIn(1);

        when(customerFeignClient.getById(1L)).thenReturn(ApiResponse.success(customer));
        when(customerFeignClient.updateById(eq(1L), any())).thenReturn(ApiResponse.success(true));

        stateSyncService.fixCustomerResidualState(1L);

        assertEquals(0, customer.getCheckedIn());
    }

    @Test
    @DisplayName("fixCustomerResidualState 客户状态正常时不应修改")
    void fixCustomerResidualState_noFixNeeded() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCheckedIn(0);

        when(customerFeignClient.getById(1L)).thenReturn(ApiResponse.success(customer));

        stateSyncService.fixCustomerResidualState(1L);

        assertEquals(0, customer.getCheckedIn());
        verify(customerFeignClient, never()).updateById(any(), any());
    }

    // ==================== fixBedResidualState ====================

    @Test
    @DisplayName("fixBedResidualState 应修复残留的床位状态")
    void fixBedResidualState_success() {
        Bed bed = new Bed();
        bed.setId(10L);
        bed.setStatus("已入住");
        bed.setCustomerId(1L);

        when(bedFeignClient.getById(10L)).thenReturn(ApiResponse.success(bed));
        when(bedFeignClient.updateById(eq(10L), any())).thenReturn(ApiResponse.success(true));

        stateSyncService.fixBedResidualState(10L);

        assertEquals("空闲", bed.getStatus());
        assertNull(bed.getCustomerId());
    }

    @Test
    @DisplayName("fixBedResidualState 床位已空闲时不应修改")
    void fixBedResidualState_noFixNeeded() {
        Bed bed = new Bed();
        bed.setId(10L);
        bed.setStatus("空闲");

        when(bedFeignClient.getById(10L)).thenReturn(ApiResponse.success(bed));

        stateSyncService.fixBedResidualState(10L);

        assertEquals("空闲", bed.getStatus());
        verify(bedFeignClient, never()).updateById(any(), any());
    }
}
