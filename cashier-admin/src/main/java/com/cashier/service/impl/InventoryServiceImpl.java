package com.cashier.service.impl;

import com.cashier.model.InventoryRecord;
import com.cashier.model.InventoryOperationType;
import com.cashier.model.Product;
import com.cashier.model.User;
import com.cashier.repository.InventoryRecordRepository;
import com.cashier.repository.ProductRepository;
import com.cashier.service.InventoryService;
import com.cashier.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRecordRepository inventoryRecordRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRecordRepository inventoryRecordRepository,
                              ProductService productService,
                              ProductRepository productRepository) {
        this.inventoryRecordRepository = inventoryRecordRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public InventoryRecord createRecord(InventoryRecord record) {
        // 获取当前登录用户
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        record.setOperator(currentUser);

        // 更新商品库存
        Product product = record.getProduct();
        int newStock = product.getStock() + record.getQuantity();
        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        product.setStock(newStock);
        productRepository.save(product);

        return inventoryRecordRepository.save(record);
    }

    @Override
    public Page<InventoryRecord> getRecordsByProductId(Long productId, Pageable pageable) {
        return inventoryRecordRepository.findByProductId(productId, pageable);
    }

    @Override
    public Page<InventoryRecord> searchRecords(Specification<InventoryRecord> spec, Pageable pageable) {
        return inventoryRecordRepository.findAll(spec, pageable);
    }

    @Override
    public List<InventoryRecord> getRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return inventoryRecordRepository.findAll((root, query, cb) ->
                cb.and(
                    cb.greaterThanOrEqualTo(root.get("operationTime"), startDate),
                    cb.lessThanOrEqualTo(root.get("operationTime"), endDate)
                ));
    }

    @Override
    @Transactional
    public InventoryRecord adjustStock(Long productId, int quantity, InventoryOperationType operationType, String remark) {
        Product product = productService.getProductById(productId);
        
        InventoryRecord record = new InventoryRecord();
        record.setProduct(product);
        record.setQuantity(quantity);
        record.setOperationType(operationType);
        record.setRemark(remark);
        
        return createRecord(record);
    }
} 