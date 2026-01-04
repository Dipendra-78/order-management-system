package com.ordermanagement.service.impl;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ordermanagement.dto.OrderMetricDto;
import com.ordermanagement.dto.OrderPerDayDto;
import com.ordermanagement.dto.OrderStatusCountDto;
import com.ordermanagement.dto.OrderStatusHistoryResponseDto;
import com.ordermanagement.dto.PageResponseDto;
import com.ordermanagement.entity.Order;
import com.ordermanagement.entity.OrderStatus;
import com.ordermanagement.entity.OrderStatusHistory;
import com.ordermanagement.entity.Role;
import com.ordermanagement.entity.User;
import com.ordermanagement.exception.InvalidOrderStateException;
import com.ordermanagement.exception.OrderNotFoundException;
import com.ordermanagement.repository.OrderRepo;
import com.ordermanagement.repository.OrderStatusHistoryRepo;
import com.ordermanagement.service.NotificationService;
import com.ordermanagement.service.OrderService;
import com.ordermanagement.specification.OrderSpecification;

import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final OrderStatusHistoryRepo historyRepo;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Order placeOrder(Order order) {

        order.setStatus(OrderStatus.CREATED);

        double price = calculatePrice(order.getProductName(), order.getQuantity());

        order.setPrice(price);

        Order savedOrder = orderRepo.save(order);

        saveHistory(savedOrder, OrderStatus.CREATED, OrderStatus.CREATED, Role.USER);

        return savedOrder;

    }

    private double calculatePrice(String productName, int quantity) {

        double basePrice;

        switch (productName.toLowerCase()) {
            case "book" -> basePrice = 500;
            case "phone" -> basePrice = 20000;
            case "laptop" -> basePrice = 60000;
            default -> basePrice = 100;
        }

        return basePrice * quantity;
    }

    @Override
    public Page<Order> getOrdersByUser(User user, Pageable pageable) {

        return orderRepo.findByUserWithUser(user, pageable);


    }

    @Override
    public Page<Order> getAllOrders(@NonNull Pageable pageable) {

        return orderRepo.findAll(pageable);
    }

    @Override
    @CacheEvict(value = { "ordersPerDay", "ordersPerDayRange" }, allEntries = true)
    @Transactional
    public Order cancelOrder(long orderId, User user) {

        Order order = orderRepo.findById(orderId).orElseThrow(() -> new OrderNotFoundException("order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can't cancel this order");
        }

        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("Order can't be cancelled now");
        }

        OrderStatus old = order.getStatus();

        order.setStatus(OrderStatus.CANCELLED);

        saveHistory(order, old, OrderStatus.CANCELLED, Role.USER);
        notificationService.orderStatusChanged(
                order.getId(),
                old.name(),
                OrderStatus.CANCELLED.name());

        return order;

    }

    @Override
    @CacheEvict(value = { "ordersPerDay", "ordersPerDayRange" }, allEntries = true)
    @Transactional
    public Order updateOrderStatus(long orderId, OrderStatus newStatus) {

        Order order = orderRepo.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));

        OrderStatus current = order.getStatus();

        if (current == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Cancelled order can't be updated");
        }

        if (newStatus == OrderStatus.CANCELLED) {
            if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
                throw new InvalidOrderStateException("Cannot cancel Shipped or Delivered Order");
            }
            order.setStatus(OrderStatus.CANCELLED);
            saveHistory(order, current, newStatus, Role.ADMIN);
            notificationService.orderStatusChanged(
                    order.getId(),
                    current.name(),
                    newStatus.name());
            return order;
        }

        boolean valid = (current == OrderStatus.CREATED && newStatus == OrderStatus.PAID) ||
                (current == OrderStatus.PAID && newStatus == OrderStatus.SHIPPED) ||
                (current == OrderStatus.SHIPPED && newStatus == OrderStatus.DELIVERED);

        if (!valid) {
            throw new InvalidOrderStateException("Invalid Status change from " + current + " to " + newStatus);
        }

        order.setStatus(newStatus);
        saveHistory(order, current, newStatus, Role.ADMIN);
        notificationService.orderStatusChanged(
                order.getId(),
                current.name(),
                newStatus.name());
        return order;
    }

    private void saveHistory(Order order, OrderStatus oldStatus, OrderStatus newStatus, Role role) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(role);
        history.setChangedAt(LocalDateTime.now());

        historyRepo.save(history);

    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<OrderStatusHistoryResponseDto> getOrderHistory(long orderId, User currentUser,
            Pageable pageable) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isAdmin && !order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to view this order history");
        }

        Page<OrderStatusHistory> page = historyRepo.findByOrderOrderByChangedAtDesc(order, pageable);

        List<OrderStatusHistoryResponseDto> data = page.getContent()
                .stream()
                .map(history -> {
                    OrderStatusHistoryResponseDto dto = new OrderStatusHistoryResponseDto();
                    dto.setOldStatus(history.getOldStatus());
                    dto.setNewStatus(history.getNewStatus());
                    dto.setChangedBy(history.getChangedBy());
                    dto.setChangedAt(history.getChangedAt());
                    return dto;
                }).toList();

        return new PageResponseDto<>(
                data,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()

        );

    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> searchOrders(User currentUser, OrderStatus status, Double min, Double max, LocalDate from,
            LocalDate to, Pageable pageable) {

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (min != null && max != null && min > max) {
            throw new IllegalArgumentException("min price can not be greater than max price");
        }

        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("from data can not be after to date");
        }

        if (pageable.getPageSize() > 50) {
            throw new IllegalArgumentException("Page size is too large");
        }

        Specification<Order> specification = Specification.where(OrderSpecification.hasStatus(status))
                .and(OrderSpecification.priceBetween(min, max)).and(OrderSpecification.createdBetween(from, to));

        if (!isAdmin) {
            specification = specification.and(OrderSpecification.hasUser(currentUser));
        }

        return orderRepo.findAll(specification, pageable);

    }

    @Override
    public OrderMetricDto getMetrics() {
        return new OrderMetricDto(
                orderRepo.count(),
                orderRepo.totalRevenue());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusCountDto> getOrderCountByStatus() {
        return orderRepo.countByStatus()
                .stream()
                .map(r -> new OrderStatusCountDto(
                        (OrderStatus) r[0],
                        (Long) r[1]))
                .toList();
    }

    @Override
    public double getRevenueBetween(LocalDateTime from, LocalDateTime to) {
        return orderRepo.revenueBetween(from, to);
    }

    @Override
    @Cacheable(value = "ordersPerDay", key = "'days:' + #days")
    @Transactional(readOnly = true)
    public List<OrderPerDayDto> getOrdersPerDay(int days) {
        LocalDateTime from = LocalDateTime.now().minusDays(days);

        return orderRepo.ordersPerDay(from)
                .stream()
                .map(r -> new OrderPerDayDto(
                        ((java.sql.Date) r[0]).toLocalDate(),
                        (Long) r[1]))
                .toList();
    }

    @Override
    @Cacheable(value = "ordersPerDayRange", key = "'range:' + #from + ':' + #to")
    @Transactional(readOnly = true)
    public List<OrderPerDayDto> getOrdersPerDayRange(LocalDateTime from, LocalDateTime to) {
        return orderRepo.ordersPerDayRange(from, to)
                .stream()
                .map(r -> new OrderPerDayDto(
                        ((java.sql.Date) r[0]).toLocalDate(),
                        (Long) r[1]))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public void exportOrdersPerDayCsv(LocalDateTime from, LocalDateTime to, HttpServletResponse response) {
        List<OrderPerDayDto> data = getOrdersPerDayRange(from, to);

        response.setContentType("text/csv");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=orders-per-day.csv");

        try (PrintWriter writer = response.getWriter()) {

            writer.println("date,count");

            for (OrderPerDayDto dto : data) {
                writer.println(dto.date() + "," + dto.count());
            }

            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export CSV");
        }

    }

}
