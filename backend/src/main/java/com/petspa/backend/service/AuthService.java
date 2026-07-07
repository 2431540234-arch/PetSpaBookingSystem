package com.petspa.backend.service;

import com.petspa.backend.dto.request.LoginRequest;
import com.petspa.backend.dto.response.AuthResponse;
import com.petspa.backend.entity.User;
import com.petspa.backend.exception.ResourceNotFoundException;
import com.petspa.backend.repository.UserRepository;
import com.petspa.backend.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AuthenticationManager authenticationManager,
                        UserRepository userRepository,
                        JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // Uy quyen viec check password (BCrypt) cho Spring Security qua CustomUserDetailsService
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Email hoac mat khau khong dung");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email hoac mat khau khong dung"));

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, user);
    }

    public User getUserById(String userId) {
        Long id = Long.valueOf(userId);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public User updateUserProfile(String userId, User request) {
        Long id = Long.valueOf(userId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Partial update - only allowed fields
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getDob() != null) user.setDob(request.getDob());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());

        // Never update these fields
        // - id (primary key)
        // - email (unique identifier)
        // - password_hash (requires special handling)
        // - role (should use separate endpoint)

        return userRepository.save(user);
    }
}
