package com.ordermanagement.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ordermanagement.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Override
    @Async
    public void orderStatusChanged(Long orderId, String oldStatus, String newStatus) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

       log.info("Order {} status changed from {} to {}", orderId, oldStatus, newStatus);
    }
}
