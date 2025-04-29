package com.cashier.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cashier.model.CashierLog;
import com.cashier.service.CashierLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cashier-logs")
public class CashierLogController {

    private final CashierLogService cashierLogService;

    @Autowired
    public CashierLogController(CashierLogService cashierLogService) {
        this.cashierLogService = cashierLogService;
    }

    @GetMapping("/{cashierId}")
    public ResponseEntity<IPage<CashierLog>> getCashierLogs(
            @PathVariable Long cashierId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CashierLog> pageParam = new Page<>(page, size);
        return ResponseEntity.ok(cashierLogService.getCashierLogs(cashierId, pageParam));
    }
} 