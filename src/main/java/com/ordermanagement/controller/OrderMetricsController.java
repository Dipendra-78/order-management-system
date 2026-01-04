package com.ordermanagement.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ordermanagement.dto.OrderMetricDto;
import com.ordermanagement.dto.OrderStatusCountDto;
import com.ordermanagement.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/admin/metrics")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class OrderMetricsController {

    private final OrderService orderService;

    @Operation(summary = "Get overall order metrics summary (ADMIN)")
    @GetMapping("/summary")
    public OrderMetricDto summary() {
        return orderService.getMetrics();
    }

    @Operation(summary = "Get order count grouped by status (ADMIN)")
    @GetMapping("/status")
    public List<OrderStatusCountDto> byStatus() {
        return orderService.getOrderCountByStatus();
    }

    @Operation(summary = "Get revenue between date range (ADMIN)")
    @GetMapping("/revenue")
    public double revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return orderService.getRevenueBetween(
                from.atStartOfDay(),
                to.atTime(23, 59, 59));
    }

}
