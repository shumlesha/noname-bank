package com.bank.authservice.service.impl;

import com.bank.authservice.dto.auth.LoginUserRequest;
import com.bank.authservice.dto.auth.LogoutRequest;
import com.bank.authservice.dto.auth.RefreshTokenRequest;
import com.bank.authservice.dto.auth.RegisterResponse;
import com.bank.authservice.dto.auth.RegisterUserRequest;
import com.bank.authservice.dto.auth.TokenDto;
import com.bank.authservice.dto.auth.TokenVerificationDto;
import com.bank.authservice.dto.user.GetUserRequest;
import com.bank.authservice.dto.user.RoleDto;
import com.bank.authservice.dto.user.SaveUserRequest;
import com.bank.authservice.dto.user.UserDto;
import com.bank.authservice.entity.UserCredentials;
import com.bank.authservice.exception.BadRequestException;
import com.bank.authservice.exception.BusinessException;
import com.bank.authservice.mapper.AuthMapper;
import com.bank.authservice.repository.UserCredentialsRepository;
import com.bank.authservice.security.service.JwtTokenProvider;
import com.bank.authservice.service.AuthService;
import com.bank.authservice.service.UserServiceClient;
import com.bank.authservice.util.JwtUtil;
import com.bank.authservice.validator.UserCredentialsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialsRepository userCredentialsRepository;
    private final UserCredentialsValidator userCredentialsValidator;
    private final UserServiceClient userServiceClient;
    private final AuthMapper authMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public RegisterResponse registerUser(RegisterUserRequest registerUserRequest) {
        userCredentialsValidator.checkUserAlreadyExists(registerUserRequest.getEmail());

        SaveUserRequest request = authMapper.toSaveUserRequest(registerUserRequest);
        UserDto savedUser = userServiceClient.saveUser(request);

        UserCredentials userCredentials = authMapper.toUserCredentials(savedUser.getId(), registerUserRequest);
        UserCredentials savedCredentials = userCredentialsRepository.save(userCredentials);

        return new RegisterResponse(savedCredentials.getUserId(), savedCredentials.getEmail());
    }

    @Override
    @Transactional
    public TokenDto loginUser(LoginUserRequest loginUserRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserRequest.getEmail(),
                        loginUserRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        GetUserRequest getUserRequest = authMapper.toGetUserRequest(loginUserRequest);
        UserDto user = userServiceClient.getUserByEmail(getUserRequest);

        return createTokenDto(user);
    }

    @Override
    @Transactional
    public TokenDto refreshToken(RefreshTokenRequest refreshTokenRequest) {
        boolean isTokenValid = jwtTokenProvider.validateRefreshToken(refreshTokenRequest.getRefreshToken());

        if (!isTokenValid) {
            throw new BadRequestException("Invalid refresh token");
        }

        String email = jwtTokenProvider.extractEmail(refreshTokenRequest.getRefreshToken());
        UserDto user = userServiceClient.getUserByEmail(new GetUserRequest(email));

        return createTokenDto(user);
    }

    @Override
    @Transactional
    public void logoutUser(LogoutRequest logoutRequest, String authHeader) {
        boolean isRefreshTokenValid = jwtTokenProvider.validateRefreshToken(logoutRequest.getRefreshToken());

        if (!isRefreshTokenValid) {
            throw new BadRequestException("Invalid refresh token");
        }

        String accessToken = JwtUtil.extractJwtFromHeader(authHeader);
        jwtTokenProvider.revokeAccessToken(accessToken);
    }

    @Override
    public TokenVerificationDto verifyAccessToken(String authHeader) {
        String accessToken = JwtUtil.extractJwtFromHeader(authHeader);

        boolean isTokenValid = jwtTokenProvider.validateAccessToken(accessToken);

        return new TokenVerificationDto(isTokenValid);
    }

    private TokenDto createTokenDto(UserDto user) {
        UUID userId = user.getId();
        String email = user.getEmail();
        List<String> roles = user.getRoles().stream().map(RoleDto::getName).toList();

        String accessToken = jwtTokenProvider.createAccessToken(userId, email, roles);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId, email);

        return new TokenDto(userId, accessToken, refreshToken);
    }
}
