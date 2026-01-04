package com.ordermanagement.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ordermanagement.dto.AdminOrderResponseDto;
import com.ordermanagement.dto.OrderResponseDto;
import com.ordermanagement.dto.PageResponseDto;
import com.ordermanagement.entity.Order;
import com.ordermanagement.entity.OrderStatus;
import com.ordermanagement.entity.Role;
import com.ordermanagement.entity.User;
import com.ordermanagement.mapper.OrderMapper;
import com.ordermanagement.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController

@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RequestMapping("/api/v1/search/orders")
public class SearchController {

    private final OrderService orderService;

    @Operation(summary = "Search orders with filters, pagination (USER / ADMIN)")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping()
    public PageResponseDto<? extends Object> searchOrder(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Page<Order> orders = orderService.searchOrders(currentUser, status, min, max, from, to,
                PageRequest.of(page, size));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (isAdmin) {
            List<AdminOrderResponseDto> data = orders.getContent().stream().map(order -> {
                return OrderMapper.toAdminDto(order);
            }).toList();

            return new PageResponseDto<>(data,
                    orders.getNumber(),
                    orders.getSize(),
                    orders.getTotalElements(),
                    orders.getTotalPages());

        } else 
            {

            List<OrderResponseDto> data = orders.getContent().stream().map(order -> {
                return OrderMapper.toUserDto(order);
            }).toList();

            return new PageResponseDto<>(data,
                    orders.getNumber(),
                    orders.getSize(),
                    orders.getTotalElements(),
                    orders.getTotalPages());
        }

    }

}
