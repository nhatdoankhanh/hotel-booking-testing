package com.navikenz.hotelbooking.controller;

import com.navikenz.hotelbooking.dto.user.UserResponse;
import com.navikenz.hotelbooking.dto.user.UserUpdateRequest;
import com.navikenz.hotelbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId, Authentication authentication) {
        ensureSelfOrAdmin(userId, authentication);
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    public UserResponse updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request, Authentication authentication) {
        ensureSelfOrAdmin(userId, authentication);
        return userService.updateUser(userId, request);
    }

    private void ensureSelfOrAdmin(Long userId, Authentication authentication) {
        boolean isAdmin = false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin && !userId.equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("You can only access your own account");
        }
    }

    @DeleteMapping("/{userId}")
    public Map<String, Object> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "User deleted successfully");
        return body;
    }
}
