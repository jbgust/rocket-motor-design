package com.rocketmotordesign.security.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class ChangePasswordRequest {

    @NotBlank
    @Email
    private String email;

    public ChangePasswordRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
