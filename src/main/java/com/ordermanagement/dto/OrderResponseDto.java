package com.ordermanagement.dto;

import java.time.LocalDateTime;

import com.ordermanagement.entity.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDto {

    private Long productId;

    private String productName;

    private int quantity;

    private OrderStatus status;

    private double price;

    private LocalDateTime createdAt;

}
