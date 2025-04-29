package com.cashier.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cashier.mapper.CashierLogMapper;
import com.cashier.model.CashierLog;
import com.cashier.service.CashierLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CashierLogServiceImpl implements CashierLogService {

    private final CashierLogMapper cashierLogMapper;

    public CashierLogServiceImpl(CashierLogMapper cashierLogMapper) {
        this.cashierLogMapper = cashierLogMapper;
    }

    @Override
    @Transactional
    public void logOperation(Long cashierId, String operationType, String operationContent, String operator) {
        CashierLog log = new CashierLog();
        log.setCashierId(cashierId);
        log.setOperationType(operationType);
        log.setOperationContent(operationContent);
        log.setOperator(operator);
        log.setOperationTime(LocalDateTime.now());
        cashierLogMapper.insert(log);
    }

    @Override
    public IPage<CashierLog> getCashierLogs(Long cashierId, Page<CashierLog> page) {
        return cashierLogMapper.selectPage(page, null);
    }
} 