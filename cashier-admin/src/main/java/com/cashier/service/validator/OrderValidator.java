package com.cashier.service.validator;

import com.cashier.dto.OrderDTO;
import com.cashier.dto.OrderItemDTO;
import com.cashier.exception.BusinessException;
import com.cashier.model.Product;
import com.cashier.model.OrderStatus;
import com.cashier.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class OrderValidator {
    private final ProductService productService;

    public void validateOrder(OrderDTO orderDTO) {
        // 验证订单项
        if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
            throw new BusinessException("订单项不能为空");
        }

        // 验证商品和库存
        for (OrderItemDTO item : orderDTO.getItems()) {
            Product product = productService.getProduct(item.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在: " + item.getProductId());
            }
            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException("商品库存不足: " + product.getName());
            }
        }

        // 验证金额
        BigDecimal totalAmount = orderDTO.getItems().stream()
                .map(item -> {
                    Product product = productService.getProduct(item.getProductId());
                    return product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (orderDTO.getDiscountAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("优惠金额不能为负数");
        }

        if (orderDTO.getDiscountAmount().compareTo(totalAmount) > 0) {
            throw new BusinessException("优惠金额不能大于订单总金额");
        }

        BigDecimal actualAmount = totalAmount.subtract(orderDTO.getDiscountAmount());
        if (actualAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("实收金额不能为负数");
        }
    }

    public void validateOrderStatus(OrderStatus status) {
        if (status == null) {
            throw new BusinessException("订单状态不能为空");
        }
    }

    public void validateOrderUpdate(OrderDTO orderDTO) {
        if (orderDTO.getId() == null) {
            throw new BusinessException("订单ID不能为空");
        }

        if (orderDTO.getStatus() != null) {
            validateOrderStatus(orderDTO.getStatus());
        }

        if (orderDTO.getDiscountAmount() != null) {
            if (orderDTO.getDiscountAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("优惠金额不能为负数");
            }
        }
    }
} 