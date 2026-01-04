package com.ordermanagement.dto;

import java.time.LocalDateTime;

import com.ordermanagement.entity.OrderStatus;
import com.ordermanagement.entity.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusHistoryResponseDto {
 private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private Role changedBy;
    private LocalDateTime changedAt;
}
