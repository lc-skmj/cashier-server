package com.cashier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cashier.model.Order;
import com.cashier.model.OrderItem;
import com.cashier.model.OrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    @Select("SELECT COUNT(*) FROM orders WHERE create_time BETWEEN #{startDate} AND #{endDate}")
    long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Select("SELECT COUNT(*) FROM orders WHERE status = #{status} AND create_time BETWEEN #{startDate} AND #{endDate}")
    long countByStatusAndDateRange(@Param("status") OrderStatus status, 
                                 @Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE create_time BETWEEN #{startDate} AND #{endDate}")
    BigDecimal sumTotalAmountByDateRange(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);

    @Select("SELECT COALESCE(SUM(actual_amount), 0) FROM orders WHERE create_time BETWEEN #{startDate} AND #{endDate}")
    BigDecimal sumActualAmountByDateRange(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);

    @Select("SELECT COALESCE(SUM(discount_amount), 0) FROM orders WHERE create_time BETWEEN #{startDate} AND #{endDate}")
    BigDecimal sumDiscountAmountByDateRange(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);

    @Select("SELECT * FROM orders WHERE create_time BETWEEN #{startDate} AND #{endDate}")
    List<Order> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                              @Param("endDate") LocalDateTime endDate);

    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItem> selectOrderItemsByOrderId(@Param("orderId") Long orderId);

    void insertOrderItem(OrderItem orderItem);

    IPage<Order> selectByCashierId(Page<Order> page, @Param("cashierId") Long cashierId);
    
    IPage<Order> selectByStatus(Page<Order> page, @Param("status") OrderStatus status);
    
    IPage<Order> selectByCashierIdAndStatus(Page<Order> page, 
            @Param("cashierId") Long cashierId, 
            @Param("status") OrderStatus status);
    
    List<Order> selectByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime);
    
    List<Order> selectByCashierIdAndCreateTimeBetween(@Param("cashierId") Long cashierId, 
            @Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime);
} 