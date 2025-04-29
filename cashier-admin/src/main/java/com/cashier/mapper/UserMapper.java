package com.cashier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cashier.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
} 