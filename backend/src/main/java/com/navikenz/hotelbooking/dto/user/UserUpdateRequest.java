package com.navikenz.hotelbooking.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String userName;
    private String mobile;
    private String address;
}
