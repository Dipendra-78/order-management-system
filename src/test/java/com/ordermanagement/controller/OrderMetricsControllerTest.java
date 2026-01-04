package com.ordermanagement.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ordermanagement.dto.OrderMetricDto;
import com.ordermanagement.service.OrderService;

@WebMvcTest(OrderMetricsController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderMetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void summary_shouldReturnMetrics() throws Exception {

        when(orderService.getMetrics())
                .thenReturn(new OrderMetricDto(10L, 5000));

        mockMvc.perform(get("/api/v1/admin/metrics/summary"))
            .andExpect(status().isOk());
    }
}

