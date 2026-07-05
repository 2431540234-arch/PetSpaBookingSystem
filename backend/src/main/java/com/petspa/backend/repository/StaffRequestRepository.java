package com.petspa.backend.repository;

import com.petspa.backend.entity.StaffRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRequestRepository extends JpaRepository<StaffRequest, String> {
    List<StaffRequest> findByStaffId(String staffId);

    List<StaffRequest> findByStatus(String status);
}

