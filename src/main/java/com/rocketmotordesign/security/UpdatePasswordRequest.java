package com.rocketmotordesign.security;

import com.rocketmotordesign.security.services.ValidPassword;

public class UpdatePasswordRequest {

    @ValidPassword
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
