package com.cashier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cashier.model.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
    
    int deleteByOrderId(@Param("orderId") Long orderId);
} 