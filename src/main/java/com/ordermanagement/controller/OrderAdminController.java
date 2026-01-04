package com.ordermanagement.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ordermanagement.dto.AdminOrderResponseDto;
import com.ordermanagement.dto.ApiResponse;
import com.ordermanagement.dto.PageResponseDto;
import com.ordermanagement.entity.Order;
import com.ordermanagement.entity.OrderStatus;
import com.ordermanagement.mapper.OrderMapper;
import com.ordermanagement.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderService orderService;

    @Operation(summary = "Mark order as PAID (ADMIN)")
    @PutMapping("/{id}/pay")
    public ApiResponse<AdminOrderResponseDto> markPaid(@PathVariable Long id) {
        return new ApiResponse<>(true, "Order marked as PAID",
                OrderMapper.toAdminDto(orderService.updateOrderStatus(id, OrderStatus.PAID)));
    }

    @Operation(summary = "Mark order as SHIPPED (ADMIN)")
    @PutMapping("/{id}/ship")
    public ApiResponse<AdminOrderResponseDto> markShip(@PathVariable Long id) {
        return new ApiResponse<>(
                true,
                "Order marked as SHIPPED",
                OrderMapper.toAdminDto(
                        orderService.updateOrderStatus(id, OrderStatus.SHIPPED)));
    }

    @Operation(summary = "Mark order as DELIVERED (ADMIN)")
    @PutMapping("/{id}/deliver")
    public ApiResponse<AdminOrderResponseDto> markDeliver(@PathVariable Long id) {
        return new ApiResponse<>(
                true,
                "Order marked as DELIVERED",
                OrderMapper.toAdminDto(
                        orderService.updateOrderStatus(id, OrderStatus.DELIVERED)));
    }

    @Operation(summary = "Cancel any order (ADMIN)")
    @PutMapping("/{id}/cancel")
    public ApiResponse<AdminOrderResponseDto> cancelOrder(@PathVariable Long id) {
        return new ApiResponse<>(
                true,
                "Order cancelled by admin",
                OrderMapper.toAdminDto(
                        orderService.updateOrderStatus(id, OrderStatus.CANCELLED)));
    }

    @Operation(summary = "Get all orders with pagination & sorting (ADMIN)")
    @GetMapping("/all")
    public PageResponseDto<AdminOrderResponseDto> getAllOrders(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        List<String> allowedSorts = List.of("createdAt", "price", "status");

        if (!allowedSorts.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<Order> orderPage = orderService.getAllOrders(pageable);

        List<AdminOrderResponseDto> data = orderPage.getContent().stream().map(order -> {
            return OrderMapper.toAdminDto(order);
        }).toList();

        return new PageResponseDto<>(data,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages());
    }

}
