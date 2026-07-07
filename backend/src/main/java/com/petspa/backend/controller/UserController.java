package com.petspa.backend.controller;

import com.petspa.backend.dto.request.FCMTokenRequest;
import com.petspa.backend.entity.User;
import com.petspa.backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Operation(summary = "Cập nhật FCM Token cho Push Notification")
    @PostMapping("/fcm-token")
    public ResponseEntity<Void> updateFcmToken(@Valid @RequestBody FCMTokenRequest request, Authentication authentication) {
        String userIdStr = authentication.getName();
        Long userId = Long.parseLong(userIdStr);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFcmToken(request.getToken());
        userRepository.save(user);
        
        return ResponseEntity.ok().build();
    }
}
