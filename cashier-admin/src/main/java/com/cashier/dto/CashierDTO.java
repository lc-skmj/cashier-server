package com.cashier.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CashierDTO {
    private Long id;
    private String employeeId;
    private String name;
    private String phone;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 