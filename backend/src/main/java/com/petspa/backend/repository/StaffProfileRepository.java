package com.petspa.backend.repository;

import com.petspa.backend.entity.StaffProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffProfileRepository extends JpaRepository<StaffProfile, String> {
    Optional<StaffProfile> findByUserId(String userId);
}

