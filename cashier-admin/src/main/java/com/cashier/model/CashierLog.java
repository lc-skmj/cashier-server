package com.cashier.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cashier_logs")
public class CashierLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long cashierId;
    private String operationType;
    private String operationContent;
    private String operator;
    private LocalDateTime operationTime;
} 