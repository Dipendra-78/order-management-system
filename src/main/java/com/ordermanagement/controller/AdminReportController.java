package com.ordermanagement.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ordermanagement.dto.ApiResponse;
import com.ordermanagement.dto.OrderPerDayDto;
import com.ordermanagement.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final OrderService orderService;

    @Operation(summary = "Get orders count per day for last N days (ADMIN)")
     @GetMapping("/orders-per-day")
   public ApiResponse<List<OrderPerDayDto>> ordersPerDay(
        @RequestParam(defaultValue = "7") int days) {

    return new ApiResponse<>(true, "Orders per day", orderService.getOrdersPerDay(days));
}


@Operation(summary = "Get orders per day within date range (ADMIN)")
    @GetMapping("/orders-per-day/range")
public List<OrderPerDayDto> ordersPerDayRange(
    @RequestParam LocalDate from,
    @RequestParam LocalDate to
) {
    if (from.isAfter(to)) {
        throw new IllegalArgumentException("from date cannot be after to date");
    }

    return orderService.getOrdersPerDayRange(
        from.atStartOfDay(),
        to.atTime(23, 59, 59)
    );
}

@Operation(summary = "Export orders-per-day report as CSV (ADMIN)")
@GetMapping("/orders-per-day/export")
public void exportOrdersPerDayCsv(
    @RequestParam LocalDate from,
    @RequestParam LocalDate to,
    HttpServletResponse response
) {
    if (from.isAfter(to)) {
        throw new IllegalArgumentException("from date cannot be after to date");
    }

    orderService.exportOrdersPerDayCsv(
        from.atStartOfDay(),
        to.atTime(23, 59, 59),
        response
    );
}



}
