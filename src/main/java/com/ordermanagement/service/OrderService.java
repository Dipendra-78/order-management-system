package com.ordermanagement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ordermanagement.dto.OrderMetricDto;
import com.ordermanagement.dto.OrderPerDayDto;
import com.ordermanagement.dto.OrderStatusCountDto;
import com.ordermanagement.dto.OrderStatusHistoryResponseDto;
import com.ordermanagement.dto.PageResponseDto;
import com.ordermanagement.entity.Order;
import com.ordermanagement.entity.OrderStatus;
import com.ordermanagement.entity.User;

import jakarta.servlet.http.HttpServletResponse;

public interface OrderService {

    Order placeOrder(Order order);

    Page<Order> getOrdersByUser(User user, Pageable pageable);

    Page<Order> getAllOrders(Pageable pageable);

    Order cancelOrder(long orderId, User user);

    Order updateOrderStatus(long orderId, OrderStatus newStatus);

    PageResponseDto<OrderStatusHistoryResponseDto> getOrderHistory(long orderId, User currentUser, Pageable pageable);

    Page<Order> searchOrders(User currentUser, OrderStatus status, Double min, Double max, LocalDate from,
            LocalDate to, Pageable pageable);

    OrderMetricDto getMetrics();

    List<OrderStatusCountDto> getOrderCountByStatus();

    double getRevenueBetween(LocalDateTime from, LocalDateTime to);

    List<OrderPerDayDto> getOrdersPerDay(int days);

    List<OrderPerDayDto> getOrdersPerDayRange(LocalDateTime from,
            LocalDateTime to);

    void exportOrdersPerDayCsv(
            LocalDateTime from,
            LocalDateTime to,
            HttpServletResponse response);

           

}
