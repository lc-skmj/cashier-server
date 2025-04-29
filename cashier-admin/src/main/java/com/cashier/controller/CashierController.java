package com.cashier.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cashier.dto.CashierDTO;
import com.cashier.model.Cashier;
import com.cashier.service.CashierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cashiers")
public class CashierController {

    private final CashierService cashierService;

    @Autowired
    public CashierController(CashierService cashierService) {
        this.cashierService = cashierService;
    }

    @PostMapping
    public ResponseEntity<CashierDTO> createCashier(@RequestBody CashierDTO cashierDTO) {
        return ResponseEntity.ok(cashierService.createCashier(cashierDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CashierDTO> updateCashier(@PathVariable Long id, @RequestBody CashierDTO cashierDTO) {
        cashierDTO.setId(id);
        return ResponseEntity.ok(cashierService.updateCashier(cashierDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CashierDTO> getCashier(@PathVariable Long id) {
        return ResponseEntity.ok(cashierService.getCashier(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCashier(@PathVariable Long id) {
        cashierService.deleteCashier(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<IPage<CashierDTO>> listCashiers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Cashier> pageParam = new Page<>(page, size);
        return ResponseEntity.ok(cashierService.listCashiers(pageParam));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CashierDTO> toggleCashierStatus(@PathVariable Long id) {
        return ResponseEntity.ok(cashierService.toggleCashierStatus(id));
    }

    @GetMapping("/search")
    public ResponseEntity<IPage<CashierDTO>> searchCashiers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Cashier> pageParam = new Page<>(page, size);
        return ResponseEntity.ok(cashierService.searchCashiers(keyword, pageParam));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<IPage<CashierDTO>> getCashiersByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Cashier> pageParam = new Page<>(page, size);
        return ResponseEntity.ok(cashierService.getCashiersByStatus(status, pageParam));
    }

    @GetMapping("/time-range")
    public ResponseEntity<IPage<CashierDTO>> getCashiersByCreateTimeRange(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Cashier> pageParam = new Page<>(page, size);
        return ResponseEntity.ok(cashierService.getCashiersByCreateTimeRange(startTime, endTime, pageParam));
    }
} 