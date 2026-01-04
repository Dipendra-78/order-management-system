package com.ordermanagement.dto;

import com.ordermanagement.entity.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusDto {

    @NotNull
    private OrderStatus status;

}
