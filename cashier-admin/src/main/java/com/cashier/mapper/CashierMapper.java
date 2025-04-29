package com.cashier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cashier.model.Cashier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CashierMapper extends BaseMapper<Cashier> {
    
    @Select("SELECT * FROM cashiers WHERE employee_id LIKE CONCAT('%', #{keyword}, '%') " +
            "OR name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR phone LIKE CONCAT('%', #{keyword}, '%')")
    IPage<Cashier> searchByKeyword(Page<Cashier> page, @Param("keyword") String keyword);

    @Select("SELECT * FROM cashiers WHERE status = #{status}")
    IPage<Cashier> findByStatus(Page<Cashier> page, @Param("status") Integer status);

    @Select("SELECT * FROM cashiers WHERE create_time BETWEEN #{startTime} AND #{endTime}")
    IPage<Cashier> findByCreateTimeRange(Page<Cashier> page, 
            @Param("startTime") String startTime, 
            @Param("endTime") String endTime);
} 