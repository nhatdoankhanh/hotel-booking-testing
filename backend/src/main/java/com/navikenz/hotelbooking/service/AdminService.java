package com.navikenz.hotelbooking.service;

import com.navikenz.hotelbooking.dto.admin.AdminRegisterRequest;
import com.navikenz.hotelbooking.dto.auth.TokenResponse;
import com.navikenz.hotelbooking.entity.Admin;

public interface AdminService {
    Admin registerAdmin(AdminRegisterRequest request);
    TokenResponse login(String email, String password);
}
