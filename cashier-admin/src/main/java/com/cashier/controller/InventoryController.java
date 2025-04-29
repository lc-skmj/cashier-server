package com.cashier.controller;

import com.cashier.model.InventoryRecord;
import com.cashier.model.InventoryOperationType;
import com.cashier.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InventoryRecord> createRecord(@RequestBody InventoryRecord record) {
        return ResponseEntity.ok(inventoryService.createRecord(record));
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<Page<InventoryRecord>> getRecordsByProductId(
            @PathVariable Long productId,
            Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getRecordsByProductId(productId, pageable));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<Page<InventoryRecord>> searchRecords(
            Specification<InventoryRecord> spec,
            Pageable pageable) {
        return ResponseEntity.ok(inventoryService.searchRecords(spec, pageable));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<InventoryRecord>> getRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(inventoryService.getRecordsByDateRange(startDate, endDate));
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InventoryRecord> adjustStock(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam InventoryOperationType operationType,
            @RequestParam(required = false) String remark) {
        return ResponseEntity.ok(inventoryService.adjustStock(productId, quantity, operationType, remark));
    }
} 