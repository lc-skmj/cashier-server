package com.cashier.service;

import com.cashier.dto.AuthRequest;
import com.cashier.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse register(AuthRequest request);
} 