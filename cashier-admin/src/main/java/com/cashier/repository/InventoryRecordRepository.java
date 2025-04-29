package com.cashier.repository;

import com.cashier.model.InventoryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRecordRepository extends JpaRepository<InventoryRecord, Long>, JpaSpecificationExecutor<InventoryRecord> {
    Page<InventoryRecord> findByProductId(Long productId, Pageable pageable);
} 