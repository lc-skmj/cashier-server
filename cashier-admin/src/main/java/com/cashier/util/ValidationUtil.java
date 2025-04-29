package com.cashier.util;

import com.cashier.exception.BusinessException;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMPLOYEE_ID_PATTERN = Pattern.compile("^[A-Za-z0-9]{6,20}$");

    public static void validatePhone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException("手机号格式不正确");
        }
    }

    public static void validateEmployeeId(String employeeId) {
        if (employeeId == null || !EMPLOYEE_ID_PATTERN.matcher(employeeId).matches()) {
            throw new BusinessException("工号格式不正确,必须是6-20位字母或数字");
        }
    }

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > 50) {
            throw new BusinessException("姓名不能为空且长度不能超过50个字符");
        }
    }
} 