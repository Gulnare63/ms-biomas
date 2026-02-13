package com.example.authmodule.service.concrete;


import com.example.authmodule.dao.entity.RefreshTokenEntity;
import com.example.authmodule.dao.entity.UserEntity;
import com.example.authmodule.dao.repo.UserRepository;
import com.example.authmodule.exception.UserNotFoundException;
import com.example.authmodule.model.request.LoginRequest;
import com.example.authmodule.model.request.RefreshTokenRequest;
import com.example.authmodule.model.response.AuthResponse;
import com.example.authmodule.service.abstraction.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenEntity.getToken())
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration)
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshTokenEntity refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.verifyExpiration(refreshToken);

        UserEntity user = refreshToken.getUser();
        String accessToken = jwtService.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration)
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        RefreshTokenEntity tokenEntity = refreshTokenService.findByToken(refreshToken);
        refreshTokenService.deleteByUser(tokenEntity.getUser());
    }
}
