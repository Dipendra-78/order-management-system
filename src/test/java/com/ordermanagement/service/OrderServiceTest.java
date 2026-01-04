package com.ordermanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ordermanagement.entity.Order;
import com.ordermanagement.entity.OrderStatus;
import com.ordermanagement.repository.OrderRepo;
import com.ordermanagement.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepo orderRepo;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void placeOrder_shouldSetCreatedStatus() {

        Order order = new Order();
        order.setProductName("book");
        order.setQuantity(2);

        when(orderRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Order saved = orderService.placeOrder(order);

        assertEquals(OrderStatus.CREATED, saved.getStatus());
    }
}

