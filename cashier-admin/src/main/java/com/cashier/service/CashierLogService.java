package com.cashier.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cashier.model.CashierLog;

public interface CashierLogService {
    /**
     * 记录收银员操作日志
     */
    void logOperation(Long cashierId, String operationType, String operationContent, String operator);

    /**
     * 分页查询收银员操作日志
     */
    IPage<CashierLog> getCashierLogs(Long cashierId, Page<CashierLog> page);
} 