package com.navikenz.hotelbooking.service;

import com.navikenz.hotelbooking.dto.auth.TokenResponse;
import com.navikenz.hotelbooking.dto.user.UserRegisterRequest;
import com.navikenz.hotelbooking.dto.user.UserResponse;
import com.navikenz.hotelbooking.dto.user.UserUpdateRequest;
import com.navikenz.hotelbooking.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(UserRegisterRequest request);
    TokenResponse login(String email, String password);
    List<UserResponse> getAllUsers();
    UserResponse getUser(Long userId);
    UserResponse updateUser(Long userId, UserUpdateRequest request);
    void deleteUser(Long userId);
}
