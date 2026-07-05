package com.petspa.backend.repository;

import com.petspa.backend.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, String> {
    Optional<NotificationSetting> findByUserId(String userId);
}

