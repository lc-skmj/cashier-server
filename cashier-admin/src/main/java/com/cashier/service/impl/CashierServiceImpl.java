package com.cashier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cashier.dto.CashierDTO;
import com.cashier.mapper.CashierMapper;
import com.cashier.model.Cashier;
import com.cashier.service.CashierService;
import com.cashier.service.CashierLogService;
import com.cashier.exception.BusinessException;
import com.cashier.util.ValidationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CashierServiceImpl implements CashierService {

    private final CashierMapper cashierMapper;
    private final CashierLogService cashierLogService;

    public CashierServiceImpl(CashierMapper cashierMapper, CashierLogService cashierLogService) {
        this.cashierMapper = cashierMapper;
        this.cashierLogService = cashierLogService;
    }

    @Override
    @Transactional
    public CashierDTO createCashier(CashierDTO cashierDTO) {
        // 数据验证
        ValidationUtil.validateEmployeeId(cashierDTO.getEmployeeId());
        ValidationUtil.validateName(cashierDTO.getName());
        ValidationUtil.validatePhone(cashierDTO.getPhone());

        // 检查工号是否已存在
        LambdaQueryWrapper<Cashier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cashier::getEmployeeId, cashierDTO.getEmployeeId());
        if (cashierMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("工号已存在");
        }

        Cashier cashier = new Cashier();
        BeanUtils.copyProperties(cashierDTO, cashier);
        cashier.setStatus(1); // 默认在职
        cashier.setCreateTime(LocalDateTime.now());
        cashier.setUpdateTime(LocalDateTime.now());

        cashierMapper.insert(cashier);
        cashierDTO.setId(cashier.getId());

        // 记录操作日志
        cashierLogService.logOperation(
            cashier.getId(),
            "CREATE",
            String.format("创建收银员: %s(%s)", cashier.getName(), cashier.getEmployeeId()),
            "SYSTEM"
        );

        return cashierDTO;
    }

    @Override
    @Transactional
    public CashierDTO updateCashier(CashierDTO cashierDTO) {
        // 数据验证
        ValidationUtil.validateEmployeeId(cashierDTO.getEmployeeId());
        ValidationUtil.validateName(cashierDTO.getName());
        ValidationUtil.validatePhone(cashierDTO.getPhone());

        Cashier cashier = cashierMapper.selectById(cashierDTO.getId());
        if (cashier == null) {
            throw new BusinessException("收银员不存在");
        }

        // 检查工号是否与其他收银员重复
        LambdaQueryWrapper<Cashier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cashier::getEmployeeId, cashierDTO.getEmployeeId())
               .ne(Cashier::getId, cashierDTO.getId());
        if (cashierMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("工号已存在");
        }

        BeanUtils.copyProperties(cashierDTO, cashier);
        cashier.setUpdateTime(LocalDateTime.now());
        cashierMapper.updateById(cashier);

        // 记录操作日志
        cashierLogService.logOperation(
            cashier.getId(),
            "UPDATE",
            String.format("更新收银员信息: %s(%s)", cashier.getName(), cashier.getEmployeeId()),
            "SYSTEM"
        );

        return cashierDTO;
    }

    @Override
    public CashierDTO getCashier(Long id) {
        Cashier cashier = cashierMapper.selectById(id);
        if (cashier == null) {
            throw new BusinessException("收银员不存在");
        }

        CashierDTO cashierDTO = new CashierDTO();
        BeanUtils.copyProperties(cashier, cashierDTO);
        return cashierDTO;
    }

    @Override
    @Transactional
    public void deleteCashier(Long id) {
        Cashier cashier = cashierMapper.selectById(id);
        if (cashier == null) {
            throw new BusinessException("收银员不存在");
        }

        cashierMapper.deleteById(id);

        // 记录操作日志
        cashierLogService.logOperation(
            id,
            "DELETE",
            String.format("删除收银员: %s(%s)", cashier.getName(), cashier.getEmployeeId()),
            "SYSTEM"
        );
    }

    @Override
    public IPage<CashierDTO> listCashiers(Page<Cashier> page) {
        return cashierMapper.selectPage(page, null)
                .convert(cashier -> {
                    CashierDTO dto = new CashierDTO();
                    BeanUtils.copyProperties(cashier, dto);
                    return dto;
                });
    }

    @Override
    @Transactional
    public CashierDTO toggleCashierStatus(Long id) {
        Cashier cashier = cashierMapper.selectById(id);
        if (cashier == null) {
            throw new BusinessException("收银员不存在");
        }

        int oldStatus = cashier.getStatus();
        cashier.setStatus(cashier.getStatus() == 1 ? 0 : 1);
        cashier.setUpdateTime(LocalDateTime.now());
        cashierMapper.updateById(cashier);

        // 记录操作日志
        cashierLogService.logOperation(
            id,
            "STATUS_CHANGE",
            String.format("收银员状态变更: %s -> %s", 
                oldStatus == 1 ? "在职" : "离职",
                cashier.getStatus() == 1 ? "在职" : "离职"),
            "SYSTEM"
        );

        CashierDTO cashierDTO = new CashierDTO();
        BeanUtils.copyProperties(cashier, cashierDTO);
        return cashierDTO;
    }

    @Override
    public IPage<CashierDTO> searchCashiers(String keyword, Page<Cashier> page) {
        return cashierMapper.searchByKeyword(page, keyword)
                .convert(cashier -> {
                    CashierDTO dto = new CashierDTO();
                    BeanUtils.copyProperties(cashier, dto);
                    return dto;
                });
    }

    @Override
    public IPage<CashierDTO> getCashiersByStatus(Integer status, Page<Cashier> page) {
        return cashierMapper.findByStatus(page, status)
                .convert(cashier -> {
                    CashierDTO dto = new CashierDTO();
                    BeanUtils.copyProperties(cashier, dto);
                    return dto;
                });
    }

    @Override
    public IPage<CashierDTO> getCashiersByCreateTimeRange(String startTime, String endTime, Page<Cashier> page) {
        return cashierMapper.findByCreateTimeRange(page, startTime, endTime)
                .convert(cashier -> {
                    CashierDTO dto = new CashierDTO();
                    BeanUtils.copyProperties(cashier, dto);
                    return dto;
                });
    }
} 