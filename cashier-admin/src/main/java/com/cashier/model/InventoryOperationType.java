package com.cashier.model;

public enum InventoryOperationType {
    PURCHASE,      // 采购入库
    RETURN,        // 退货入库
    SALE,          // 销售出库
    DAMAGE,        // 损坏出库
    ADJUSTMENT,    // 库存调整
    CHECK          // 盘点
} 