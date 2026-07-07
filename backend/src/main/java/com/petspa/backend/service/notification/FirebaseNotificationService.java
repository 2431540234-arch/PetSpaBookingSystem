package com.petspa.backend.service.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.petspa.backend.entity.User;
import com.petspa.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class FirebaseNotificationService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseNotificationService.class);

    private final UserRepository userRepository;

    @Value("${app.firebase.config-path:}")
    private String firebaseConfigPath;

    public FirebaseNotificationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initialize() {
        if (firebaseConfigPath == null || firebaseConfigPath.isEmpty()) {
            log.warn("Firebase config path not provided. FCM will not work.");
            return;
        }
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(firebaseConfigPath)))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase has been initialized");
            }
        } catch (IOException e) {
            log.error("Error initializing Firebase", e);
        }
    }

    public void sendToUser(Long userId, String title, String body, Map<String, String> data) {
        userRepository.findById(userId).ifPresent(user -> {
            if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
                sendPush(user.getFcmToken(), title, body, data);
            }
        });
    }

    public void sendToTopic(String topic, String title, String body, Map<String, String> data) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(notification)
                .putAllData(data)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
            log.info("Sent message to topic: {}", topic);
        } catch (FirebaseMessagingException e) {
            log.error("Error sending message to topic: {}", topic, e);
        }
    }

    public void sendToRole(com.petspa.backend.enums.UserRole role, String title, String body, Map<String, String> data) {
        userRepository.findByRole(role).forEach(user -> {
            if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
                sendPush(user.getFcmToken(), title, body, data);
            }
        });
    }

    private void sendPush(String token, String title, String body, Map<String, String> data) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(data)
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setClickAction("petspa://payment/callback") // Example
                                .build())
                        .build())
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
            log.info("Sent message to token: {}", token);
        } catch (FirebaseMessagingException e) {
            log.error("Error sending message to token: {}", token, e);
        }
    }

    public void sendBookingNotification(Long userId, String bookingId, String status, String title, String body) {
        Map<String, String> data = Map.of(
            "type", "BOOKING",
            "bookingId", bookingId,
            "status", status
        );
        sendToUser(userId, title, body, data);
    }

    public void sendPaymentNotification(Long userId, String bookingId, String status, String title, String body) {
        Map<String, String> data = Map.of(
            "type", "PAYMENT",
            "bookingId", bookingId,
            "status", status
        );
        sendToUser(userId, title, body, data);
    }
}
