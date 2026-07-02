package com.navikenz.hotelbooking.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRegisterRequest {

    @NotBlank
    private String adminName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
