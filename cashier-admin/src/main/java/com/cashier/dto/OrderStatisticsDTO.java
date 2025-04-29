package com.cashier.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderStatisticsDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long totalOrders;
    private long pendingOrders;
    private long paidOrders;
    private long cancelledOrders;
    private long refundedOrders;
    private BigDecimal totalAmount;
    private BigDecimal actualAmount;
    private BigDecimal discountAmount;
    private BigDecimal averageOrderValue;
} 