package com.example.authmodule.service.abstraction;


import com.example.authmodule.model.request.LoginRequest;
import com.example.authmodule.model.request.RefreshTokenRequest;
import com.example.authmodule.model.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String refreshToken);
}
