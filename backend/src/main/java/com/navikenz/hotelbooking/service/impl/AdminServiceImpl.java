package com.navikenz.hotelbooking.service.impl;

import com.navikenz.hotelbooking.dto.admin.AdminRegisterRequest;
import com.navikenz.hotelbooking.dto.auth.TokenResponse;
import com.navikenz.hotelbooking.entity.Admin;
import com.navikenz.hotelbooking.exception.BadRequestException;
import com.navikenz.hotelbooking.repository.AdminRepository;
import com.navikenz.hotelbooking.security.JwtService;
import com.navikenz.hotelbooking.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public Admin registerAdmin(AdminRegisterRequest request) {
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        Admin admin = new Admin();
        admin.setAdminName(request.getAdminName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
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
        TokenResponse response = new TokenResponse();
        response.setAccessToken(token);
        response.setTokenType("bearer");
        response.setRole("admin");
        response.setUserId(admin.getAdminId());
        response.setUserName(admin.getAdminName());
        return response;
    }
}
