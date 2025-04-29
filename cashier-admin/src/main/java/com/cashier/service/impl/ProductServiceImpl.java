package com.cashier.service.impl;

import com.cashier.model.Product;
import com.cashier.repository.ProductRepository;
import com.cashier.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.existsByBarcode(product.getBarcode())) {
            throw new RuntimeException("Product with this barcode already exists");
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!existingProduct.getBarcode().equals(product.getBarcode()) 
                && productRepository.existsByBarcode(product.getBarcode())) {
            throw new RuntimeException("Product with this barcode already exists");
        }

        existingProduct.setBarcode(product.getBarcode());
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCost(product.getCost());
        existingProduct.setStock(product.getStock());
        existingProduct.setUnit(product.getUnit());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setSupplier(product.getSupplier());
        existingProduct.setActive(product.isActive());

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public Product getProductByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> searchProducts(Specification<Product> spec, Pageable pageable) {
        return productRepository.findAll(spec, pageable);
    }

    @Override
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findAll((root, query, cb) -> 
                cb.and(
                    cb.lessThanOrEqualTo(root.get("stock"), threshold),
                    cb.isTrue(root.get("active"))
                ));
    }

    @Override
    @Transactional
    public Product updateStock(Long id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        
        product.setStock(newStock);
        return productRepository.save(product);
    }
} 