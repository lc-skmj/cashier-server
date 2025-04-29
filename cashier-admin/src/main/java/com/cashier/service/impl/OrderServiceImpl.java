package com.cashier.service.impl;

import com.cashier.service.OrderService;
import com.cashier.model.Order;
import com.cashier.model.OrderItem;
import com.cashier.model.OrderStatus;
import com.cashier.dto.OrderDTO;
import com.cashier.dto.OrderItemDTO;
import com.cashier.dto.OrderStatisticsDTO;
import com.cashier.mapper.OrderMapper;
import com.cashier.mapper.OrderItemMapper;
import com.cashier.mapper.ProductMapper;
import com.cashier.exception.BusinessException;
import com.cashier.service.validator.OrderValidator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderValidator orderValidator;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        // 验证订单数据
        orderValidator.validateOrder(orderDTO);

        // 创建订单
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        order.setStatus(OrderStatus.PENDING);
        order.setCreateTime(LocalDateTime.now());
        
        // 保存订单
        orderMapper.insert(order);

        // 创建订单项
        List<OrderItem> items = orderDTO.getItems().stream()
            .map(itemDTO -> {
                OrderItem item = new OrderItem();
                BeanUtils.copyProperties(itemDTO, item);
                item.setOrderId(order.getId());
                return item;
            })
            .collect(Collectors.toList());

        // 保存订单项
        items.forEach(item -> orderItemMapper.insert(item));

        // 计算订单总金额
        BigDecimal totalAmount = items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 更新订单金额
        order.setTotalAmount(totalAmount);
        order.setActualAmount(totalAmount.subtract(orderDTO.getDiscountAmount() != null ? orderDTO.getDiscountAmount() : BigDecimal.ZERO));
        order.setDiscountAmount(orderDTO.getDiscountAmount() != null ? orderDTO.getDiscountAmount() : BigDecimal.ZERO);
        
        orderMapper.updateById(order);

        return convertToDTO(order, items);
    }

    @Override
    public OrderDTO getOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("Order not found");
        }
        List<OrderItem> items = orderItemMapper.selectByOrderId(id);
        return convertToDTO(order, items);
    }

    @Override
    public IPage<OrderDTO> listOrders(Long cashierId, OrderStatus status, int page, int size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (cashierId != null) {
            wrapper.eq(Order::getCashierId, cashierId);
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> orderPage = new Page<>(page, size);
        orderMapper.selectPage(orderPage, wrapper);

        return orderPage.convert(order -> {
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            return convertToDTO(order, items);
        });
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("Order not found");
        }
        
        order.setStatus(status);
        if (status == OrderStatus.COMPLETED) {
            order.setCompleteTime(LocalDateTime.now());
        }
        
        orderMapper.updateById(order);
        List<OrderItem> items = orderItemMapper.selectByOrderId(id);
        return convertToDTO(order, items);
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(Long id) {
        return updateOrderStatus(id, OrderStatus.CANCELLED);
    }

    @Override
    @Transactional
    public OrderDTO completeOrder(Long id) {
        return updateOrderStatus(id, OrderStatus.COMPLETED);
    }

    @Override
    public OrderStatisticsDTO getOrderStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        OrderStatisticsDTO statistics = new OrderStatisticsDTO();
        statistics.setStartTime(startTime);
        statistics.setEndTime(endTime);

        // 统计订单数量
        statistics.setTotalOrders(orderMapper.countByDateRange(startTime, endTime));
        statistics.setPendingOrders(orderMapper.countByStatusAndDateRange(OrderStatus.PENDING, startTime, endTime));
        statistics.setPaidOrders(orderMapper.countByStatusAndDateRange(OrderStatus.PAID, startTime, endTime));
        statistics.setCancelledOrders(orderMapper.countByStatusAndDateRange(OrderStatus.CANCELLED, startTime, endTime));
        statistics.setRefundedOrders(orderMapper.countByStatusAndDateRange(OrderStatus.REFUNDED, startTime, endTime));

        // 统计金额
        statistics.setTotalAmount(orderMapper.sumTotalAmountByDateRange(startTime, endTime));
        statistics.setActualAmount(orderMapper.sumActualAmountByDateRange(startTime, endTime));
        statistics.setDiscountAmount(orderMapper.sumDiscountAmountByDateRange(startTime, endTime));

        // 计算平均订单金额
        if (statistics.getTotalOrders() > 0) {
            statistics.setAverageOrderValue(statistics.getTotalAmount()
                    .divide(BigDecimal.valueOf(statistics.getTotalOrders()), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            statistics.setAverageOrderValue(BigDecimal.ZERO);
        }

        return statistics;
    }

    @Override
    public List<OrderStatisticsDTO> getDailyStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<Order> orders = orderMapper.findByDateRange(startTime, endTime);
        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreateTime().toLocalDate(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                orderList -> {
                                    OrderStatisticsDTO stats = new OrderStatisticsDTO();
                                    LocalDateTime dayStart = orderList.get(0).getCreateTime().with(LocalTime.MIN);
                                    LocalDateTime dayEnd = orderList.get(0).getCreateTime().with(LocalTime.MAX);
                                    stats.setStartTime(dayStart);
                                    stats.setEndTime(dayEnd);
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

    private OrderDTO convertToDTO(Order order, List<OrderItem> items) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);
        dto.setItems(items.stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    BeanUtils.copyProperties(item, itemDTO);
                    return itemDTO;
                })
                .collect(Collectors.toList()));
        return dto;
    }
} 