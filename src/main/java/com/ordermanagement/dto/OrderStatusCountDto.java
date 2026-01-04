package com.ordermanagement.dto;

import com.ordermanagement.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderStatusCountDto {
    private OrderStatus status;
    private long count;
}



