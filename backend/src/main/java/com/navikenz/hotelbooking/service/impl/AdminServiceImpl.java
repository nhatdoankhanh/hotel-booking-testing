package com.navikenz.hotelbooking.service.impl;

import com.navikenz.hotelbooking.dto.admin.AdminRegisterRequest;
import com.navikenz.hotelbooking.dto.auth.TokenResponse;
import com.navikenz.hotelbooking.entity.Admin;
import com.navikenz.hotelbooking.exception.BadRequestException;
import com.navikenz.hotelbooking.repository.AdminRepository;
import com.navikenz.hotelbooking.security.JwtService;
import com.navikenz.hotelbooking.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public Admin registerAdmin(AdminRegisterRequest request) {
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        Admin admin = Admin.builder()
                .adminName(request.getAdminName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        return adminRepository.save(admin);
    }

    @Override
    public TokenResponse login(String email, String password) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        String token = jwtService.generateToken(admin.getAdminId(), "admin");
        return TokenResponse.builder()
                .accessToken(token)
                .tokenType("bearer")
                .role("admin")
                .userId(admin.getAdminId())
                .userName(admin.getAdminName())
                .build();
    }
}
