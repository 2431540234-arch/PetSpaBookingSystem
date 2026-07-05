package com.petspa.backend.service;

import com.petspa.backend.dto.request.LoginRequest;
import com.petspa.backend.dto.response.AuthResponse;
import com.petspa.backend.entity.User;
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
}
