package com.cashier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cashier.model.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
} 