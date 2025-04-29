package com.cashier.repository;

import com.cashier.model.Order;
import com.cashier.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    
    Page<Order> findByCashierId(Long cashierId, Pageable pageable);
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    Page<Order> findByCashierIdAndStatus(Long cashierId, OrderStatus status, Pageable pageable);
    
    List<Order> findByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    List<Order> findByCashierIdAndCreateTimeBetween(Long cashierId, LocalDateTime startTime, LocalDateTime endTime);
} 