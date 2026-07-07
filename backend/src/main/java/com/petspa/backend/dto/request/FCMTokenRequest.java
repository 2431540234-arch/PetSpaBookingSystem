package com.petspa.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public class FCMTokenRequest {
    @NotBlank
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
