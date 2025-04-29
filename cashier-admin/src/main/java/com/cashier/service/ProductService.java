package com.cashier.service;

import com.cashier.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    Product getProductById(Long id);
    Product getProductByBarcode(String barcode);
    Page<Product> getAllProducts(Pageable pageable);
    Page<Product> searchProducts(Specification<Product> spec, Pageable pageable);
    List<Product> getLowStockProducts(int threshold);
    Product updateStock(Long id, int quantity);
    Product getProduct(Long id);
} 