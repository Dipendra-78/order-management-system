package com.ordermanagement.repository;

import org.springframework.stereotype.Repository;

import com.ordermanagement.entity.Order;
import com.ordermanagement.entity.OrderStatusHistory;
 

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OrderStatusHistoryRepo extends JpaRepository<OrderStatusHistory,Long> {

    Page<OrderStatusHistory> findByOrderOrderByChangedAtDesc(Order order,Pageable pageable);

}
