package com.ordermanagement.mapper;

import com.ordermanagement.dto.AdminOrderResponseDto;
import com.ordermanagement.dto.OrderResponseDto;
import com.ordermanagement.entity.Order;

public class OrderMapper {

    public static OrderResponseDto toUserDto(Order order)
    {
    OrderResponseDto responseDto= new OrderResponseDto();
        responseDto.setProductId(order.getId());
        responseDto.setProductName(order.getProductName());
        responseDto.setQuantity(order.getQuantity());
        responseDto.setPrice(order.getPrice());
        responseDto.setStatus(order.getStatus());
        responseDto.setCreatedAt(order.getCreatedAt()); 

        return responseDto;

    }

    public static AdminOrderResponseDto toAdminDto(Order order)
    {
        AdminOrderResponseDto adminResponseDto= new AdminOrderResponseDto();

        adminResponseDto.setProductId(order.getId());
        adminResponseDto.setProductName(order.getProductName());
        adminResponseDto.setQuantity(order.getQuantity());
        adminResponseDto.setPrice(order.getPrice());
        adminResponseDto.setStatus(order.getStatus());
        adminResponseDto.setUserid(order.getUser().getId());
        adminResponseDto.setUsername(order.getUser().getUsername());
        adminResponseDto.setCreatedAt(order.getCreatedAt());

        return adminResponseDto;
    }


}
