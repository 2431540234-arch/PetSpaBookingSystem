package com.petspa.backend.dto.response;

import com.petspa.backend.entity.User;

// Khop dung shape "data class AuthResponse(val token: String, val user: User)" phia Android
public class AuthResponse {

    private String token;
    private User user;

    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
