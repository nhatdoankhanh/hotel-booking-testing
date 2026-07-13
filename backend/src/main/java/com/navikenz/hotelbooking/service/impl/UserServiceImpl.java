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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public User registerUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMobile(request.getMobile());
        user.setAddress(request.getAddress());
        user.setRole("customer");
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
        TokenResponse response = new TokenResponse();
        response.setAccessToken(token);
        response.setTokenType("bearer");
        response.setRole("customer");
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName());
        return response;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserResponse> responses = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            responses.add(toResponse(user));
        }
        return responses;
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
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setMobile(user.getMobile());
        response.setAddress(user.getAddress());
        return response;
    }
}
