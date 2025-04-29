package com.cashier.dto;

import com.cashier.model.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Long cashierId;
    private String cashierName;
    private BigDecimal totalAmount;
    private BigDecimal actualAmount;
    private BigDecimal discountAmount;
    private OrderStatus status;
    private String remark;
    private List<OrderItemDTO> items;
    private LocalDateTime createTime;
    private LocalDateTime completeTime;
} 