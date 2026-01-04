package com.ordermanagement.service;

public interface NotificationService {
    void orderStatusChanged(Long orderId, String oldStatus, String newStatus);

}
