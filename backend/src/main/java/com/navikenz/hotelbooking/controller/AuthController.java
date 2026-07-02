package com.navikenz.hotelbooking.controller;

import com.navikenz.hotelbooking.dto.admin.AdminRegisterRequest;
import com.navikenz.hotelbooking.dto.auth.LoginRequest;
import com.navikenz.hotelbooking.dto.auth.TokenResponse;
import com.navikenz.hotelbooking.dto.user.UserRegisterRequest;
import com.navikenz.hotelbooking.entity.Admin;
import com.navikenz.hotelbooking.entity.User;
import com.navikenz.hotelbooking.service.AdminService;
import com.navikenz.hotelbooking.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@SecurityRequirements
public class AuthController {

    private final UserService userService;
    private final AdminService adminService;

    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody UserRegisterRequest request) {
        User user = userService.registerUser(request);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Registration successful");
        body.put("user_id", user.getUserId());
        body.put("email", user.getEmail());
        return body;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/admin/register")
    public Map<String, Object> adminRegister(@Valid @RequestBody AdminRegisterRequest request) {
        Admin admin = adminService.registerAdmin(request);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Admin registered");
        body.put("admin_id", admin.getAdminId());
        return body;
    }

    @PostMapping("/admin/login")
    public TokenResponse adminLogin(@Valid @RequestBody LoginRequest request) {
        return adminService.login(request.getEmail(), request.getPassword());
    }
}
