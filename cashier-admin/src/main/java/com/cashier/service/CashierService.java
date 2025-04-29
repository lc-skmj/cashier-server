package com.cashier.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cashier.dto.CashierDTO;
import com.cashier.model.Cashier;

public interface CashierService {
    /**
     * 创建收银员
     */
    CashierDTO createCashier(CashierDTO cashierDTO);

    /**
     * 更新收银员信息
     */
    CashierDTO updateCashier(CashierDTO cashierDTO);

    /**
     * 获取收银员信息
     */
    CashierDTO getCashier(Long id);

    /**
     * 删除收银员
     */
    void deleteCashier(Long id);

    /**
     * 分页查询收银员列表
     */
    IPage<CashierDTO> listCashiers(Page<Cashier> page);

    /**
     * 切换收银员状态
     */
    CashierDTO toggleCashierStatus(Long id);

    /**
     * 根据关键字搜索收银员
     */
    IPage<CashierDTO> searchCashiers(String keyword, Page<Cashier> page);

    /**
     * 根据状态查询收银员
     */
    IPage<CashierDTO> getCashiersByStatus(Integer status, Page<Cashier> page);

    /**
     * 根据创建时间范围查询收银员
     */
    IPage<CashierDTO> getCashiersByCreateTimeRange(String startTime, String endTime, Page<Cashier> page);
} 