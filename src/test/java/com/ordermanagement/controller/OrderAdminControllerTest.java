package com.ordermanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ordermanagement.entity.Order;
import com.ordermanagement.entity.OrderStatus;
import com.ordermanagement.service.OrderService;

@WebMvcTest(OrderAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void markPaid_shouldReturnUpdatedOrder() throws Exception {

        Order order = new Order();
        order.setStatus(OrderStatus.PAID);

        when(orderService.updateOrderStatus(anyLong(), any()))
                .thenReturn(order);

        mockMvc.perform(put("/api/v1/admin/orders/1/pay"))
            .andExpect(status().isOk());
    }
}

