package com.navikenz.hotelbooking.controller;

import com.navikenz.hotelbooking.dto.user.UserResponse;
import com.navikenz.hotelbooking.dto.user.UserUpdateRequest;
import com.navikenz.hotelbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    public UserResponse updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public Map<String, Object> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "User deleted successfully");
        return body;
    }
}
