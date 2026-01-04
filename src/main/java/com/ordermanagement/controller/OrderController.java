package com.ordermanagement.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ordermanagement.dto.ApiResponse;
import com.ordermanagement.dto.OrderRequestDto;
import com.ordermanagement.dto.OrderResponseDto;
import com.ordermanagement.dto.OrderStatusHistoryResponseDto;
import com.ordermanagement.dto.PageResponseDto;
import com.ordermanagement.entity.Order;
import com.ordermanagement.entity.User;
import com.ordermanagement.mapper.OrderMapper;
import com.ordermanagement.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place a new order (USER)")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<OrderResponseDto> placeOrder(@Valid @RequestBody OrderRequestDto requestDto) {

        User user = currentUser();

        Order order = new Order();
        order.setProductName(requestDto.getProductName());
        order.setQuantity(requestDto.getQuantity());
        order.setUser(user);

        Order savedOrder = orderService.placeOrder(order);

        OrderResponseDto dto = OrderMapper.toUserDto(savedOrder);

        return new ApiResponse<OrderResponseDto>(true, "Order placed Successfully", dto);

    }

    @GetMapping
    @Operation(summary = "Get logged-in user's orders with pagination & sorting")

    @PreAuthorize("hasRole('USER')")
    public PageResponseDto<OrderResponseDto> getOrdersByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction

    ) {
        User user = currentUser();

        List<String> allowedSorts = List.of("createdAt", "price", "status");

        if (!allowedSorts.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        PageRequest pagable = PageRequest.of(page, size, sort);

        Page<Order> orderPage = orderService.getOrdersByUser(user, pagable);

        List<OrderResponseDto> data = orderPage.getContent().stream()
                .map(order -> {
                    return OrderMapper.toUserDto(order);
                }).toList();

        return new PageResponseDto<>(data,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages());
    }

    @Operation(summary = "Cancel an order placed by the user")
    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<OrderResponseDto> cancelOrder(@PathVariable Long orderId) {
        User user = currentUser();

        Order order = orderService.cancelOrder(orderId, user);

        OrderResponseDto dto = OrderMapper.toUserDto(order);

        return new ApiResponse<OrderResponseDto>(true, "Order cancelled successfully", dto);

    }

    @Operation(summary = "Get order status history (USER / ADMIN)")
    @GetMapping("/{orderId}/history")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public PageResponseDto<OrderStatusHistoryResponseDto> getHistory(@PathVariable Long orderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        User user = currentUser();

        return orderService.getOrderHistory(orderId, user,
                PageRequest.of(page, size));
    }

    private User currentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

}
