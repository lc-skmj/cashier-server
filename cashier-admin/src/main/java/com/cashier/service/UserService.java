package com.cashier.service;

import com.cashier.model.User;
import com.cashier.model.UserRole;

import java.util.List;

public interface UserService {
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    User getUserById(Long id);
    List<User> getAllUsers();
    User changeUserRole(Long id, UserRole newRole);
    User changeUserStatus(Long id, boolean enabled);
} 