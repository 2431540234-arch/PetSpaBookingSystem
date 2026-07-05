package com.petspa.backend.repository;

import com.petspa.backend.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, String> {
    List<Shift> findByStaffId(String staffId);

    List<Shift> findByDate(String date);

    List<Shift> findByStatus(String status);

    List<Shift> findByStaffIdAndDate(String staffId, String date);
}

