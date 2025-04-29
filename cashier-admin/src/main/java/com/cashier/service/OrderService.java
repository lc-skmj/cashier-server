package com.cashier.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cashier.dto.OrderDTO;
import com.cashier.dto.OrderItemDTO;
import com.cashier.dto.OrderStatisticsDTO;
import com.cashier.mapper.OrderItemMapper;
import com.cashier.mapper.OrderMapper;
import com.cashier.mapper.ProductMapper;
import com.cashier.mapper.UserMapper;
import com.cashier.model.Order;
import com.cashier.model.OrderItem;
import com.cashier.model.OrderStatus;
import com.cashier.model.Product;
import com.cashier.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        // 创建订单
        Order order = new Order();
        order.setCashierId(orderDTO.getCashierId());
        order.setStatus(OrderStatus.PENDING);
        order.setRemark(orderDTO.getRemark());
        order.setOrderNumber("ORD" + System.currentTimeMillis());
        
        // 计算订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productMapper.selectById(itemDTO.getProductId());
            if (product == null) {
                throw new RuntimeException("Product not found: " + itemDTO.getProductId());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);
        order.setActualAmount(totalAmount.subtract(orderDTO.getDiscountAmount()));
        order.setDiscountAmount(orderDTO.getDiscountAmount());

        // 保存订单
        orderMapper.insert(order);

        // 保存订单项
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productMapper.selectById(itemDTO.getProductId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            orderItemMapper.insert(orderItem);
        }

        return convertToDTO(order);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("Order not found: " + id);
        }
        return convertToDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrders(Page<Order> page) {
        Page<Order> orderPage = orderMapper.selectPage(page, null);
        return orderPage.convert(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByStatus(OrderStatus status, Page<Order> page) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getStatus, status);
        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);
        return orderPage.convert(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByCashier(Long cashierId, Page<Order> page) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getCashierId, cashierId);
        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);
        return orderPage.convert(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate, Page<Order> page) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Order::getCreateTime, startDate, endDate);
        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);
        return orderPage.convert(this::convertToDTO);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("Order not found: " + id);
        }
        
        order.setStatus(status);
        if (status == OrderStatus.PAID || status == OrderStatus.CANCELLED) {
            order.setCompleteTime(LocalDateTime.now());
        }
        
        orderMapper.updateById(order);
        return convertToDTO(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (orderMapper.selectById(id) == null) {
            throw new RuntimeException("Order not found: " + id);
        }
        orderMapper.deleteById(id);
    }

    @Transactional(readOnly = true)
    public OrderStatisticsDTO getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        OrderStatisticsDTO statistics = new OrderStatisticsDTO();
        statistics.setStartTime(startDate);
        statistics.setEndTime(endDate);

        // 统计订单数量
        statistics.setTotalOrders(orderMapper.countByDateRange(startDate, endDate));
        statistics.setPendingOrders(orderMapper.countByStatusAndDateRange(OrderStatus.PENDING.name(), startDate, endDate));
        statistics.setPaidOrders(orderMapper.countByStatusAndDateRange(OrderStatus.PAID.name(), startDate, endDate));
        statistics.setCancelledOrders(orderMapper.countByStatusAndDateRange(OrderStatus.CANCELLED.name(), startDate, endDate));
        statistics.setRefundedOrders(orderMapper.countByStatusAndDateRange(OrderStatus.REFUNDED.name(), startDate, endDate));

        // 统计金额
        statistics.setTotalAmount(orderMapper.sumTotalAmountByDateRange(startDate, endDate));
        statistics.setActualAmount(orderMapper.sumActualAmountByDateRange(startDate, endDate));
        statistics.setDiscountAmount(orderMapper.sumDiscountAmountByDateRange(startDate, endDate));

        // 计算平均订单金额
        if (statistics.getTotalOrders() > 0) {
            statistics.setAverageOrderValue(statistics.getTotalAmount()
                    .divide(BigDecimal.valueOf(statistics.getTotalOrders()), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            statistics.setAverageOrderValue(BigDecimal.ZERO);
        }

        return statistics;
    }

    @Transactional(readOnly = true)
    public List<OrderStatisticsDTO> getDailyStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderMapper.findByDateRange(startDate, endDate);
        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreateTime().toLocalDate(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                orderList -> {
                                    OrderStatisticsDTO stats = new OrderStatisticsDTO();
                                    stats.setStartTime(orderList.get(0).getCreateTime().withHour(0).withMinute(0).withSecond(0));
                                    stats.setEndTime(orderList.get(0).getCreateTime().withHour(23).withMinute(59).withSecond(59));
                                    stats.setTotalOrders(orderList.size());
                                    stats.setPendingOrders(orderList.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count());
                                    stats.setPaidOrders(orderList.stream().filter(o -> o.getStatus() == OrderStatus.PAID).count());
                                    stats.setCancelledOrders(orderList.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count());
                                    stats.setRefundedOrders(orderList.stream().filter(o -> o.getStatus() == OrderStatus.REFUNDED).count());
                                    stats.setTotalAmount(orderList.stream().map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
                                    stats.setActualAmount(orderList.stream().map(Order::getActualAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
                                    stats.setDiscountAmount(orderList.stream().map(Order::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
                                    stats.setAverageOrderValue(stats.getTotalOrders() > 0 ? 
                                            stats.getTotalAmount().divide(BigDecimal.valueOf(stats.getTotalOrders()), 2, BigDecimal.ROUND_HALF_UP) : 
                                            BigDecimal.ZERO);
                                    return stats;
                                }
                        )
                ))
                .values()
                .stream()
                .sorted((a, b) -> b.getStartTime().compareTo(a.getStartTime()))
                .collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCashierId(order.getCashierId());
        
        // 获取收银员信息
        User cashier = userMapper.selectById(order.getCashierId());
        if (cashier != null) {
            dto.setCashierName(cashier.getName());
        }
        
        dto.setTotalAmount(order.getTotalAmount());
        dto.setActualAmount(order.getActualAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setStatus(order.getStatus());
        dto.setRemark(order.getRemark());
        dto.setCreateTime(order.getCreateTime());
        dto.setCompleteTime(order.getCompleteTime());

        // 获取订单项
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, order.getId());
        List<OrderItem> items = orderItemMapper.selectList(wrapper);
        
        dto.setItems(items.stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProductId());
            
            // 获取商品信息
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                itemDTO.setProductName(product.getName());
            }
            
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setUnitPrice(item.getUnitPrice());
            itemDTO.setSubtotal(item.getSubtotal());
            return itemDTO;
        }).collect(Collectors.toList()));

        return dto;
    }
} 