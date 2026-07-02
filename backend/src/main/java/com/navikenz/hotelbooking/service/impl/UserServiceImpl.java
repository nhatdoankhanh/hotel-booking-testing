package com.navikenz.hotelbooking.service.impl;

import com.navikenz.hotelbooking.dto.auth.TokenResponse;
import com.navikenz.hotelbooking.dto.user.UserRegisterRequest;
import com.navikenz.hotelbooking.dto.user.UserResponse;
import com.navikenz.hotelbooking.dto.user.UserUpdateRequest;
import com.navikenz.hotelbooking.entity.User;
import com.navikenz.hotelbooking.exception.BadRequestException;
import com.navikenz.hotelbooking.exception.ResourceNotFoundException;
import com.navikenz.hotelbooking.repository.UserRepository;
import com.navikenz.hotelbooking.security.JwtService;
import com.navikenz.hotelbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public User registerUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        User user = User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobile(request.getMobile())
                .address(request.getAddress())
                .role("customer")
                .build();
        return userRepository.save(user);
    }

    @Override
    public TokenResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        String token = jwtService.generateToken(user.getUserId(), "customer");
        return TokenResponse.builder()
                .accessToken(token)
                .tokenType("bearer")
                .role("customer")
                .userId(user.getUserId())
                .userName(user.getUserName())
                .build();
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public UserResponse getUser(Long userId) {
        return toResponse(findUser(userId));
    }

    @Override
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = findUser(userId);
        if (request.getUserName() != null) user.setUserName(request.getUserName());
        if (request.getMobile() != null) user.setMobile(request.getMobile());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        return toResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        User user = findUser(userId);
        userRepository.delete(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .role(user.getRole())
                .mobile(user.getMobile())
                .address(user.getAddress())
                .build();
    }
}
