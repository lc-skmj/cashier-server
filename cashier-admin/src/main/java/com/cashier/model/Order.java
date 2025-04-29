package com.cashier.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_number")
    private String orderNumber;

    @TableField("cashier_id")
    private Long cashierId;

    @TableField("cashier_name")
    private String cashierName;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("actual_amount")
    private BigDecimal actualAmount;

    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("status")
    private OrderStatus status;

    @TableField("remark")
    private String remark;

    @TableField(exist = false)
    private List<OrderItem> items;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField("complete_time")
    private LocalDateTime completeTime;

    @TableField(exist = false)
    private User cashier;
} 