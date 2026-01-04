package com.ordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderMetricDto {

    private long totalOrders;
    private double totalRevenue;
}



