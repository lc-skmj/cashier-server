package com.cashier.service;

import com.cashier.model.InventoryRecord;
import com.cashier.model.InventoryOperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryService {
    InventoryRecord createRecord(InventoryRecord record);
    Page<InventoryRecord> getRecordsByProductId(Long productId, Pageable pageable);
    Page<InventoryRecord> searchRecords(Specification<InventoryRecord> spec, Pageable pageable);
    List<InventoryRecord> getRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    InventoryRecord adjustStock(Long productId, int quantity, InventoryOperationType operationType, String remark);
} 