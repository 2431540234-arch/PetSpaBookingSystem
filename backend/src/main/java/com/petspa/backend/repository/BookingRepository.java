package com.petspa.backend.repository;

import com.petspa.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByStaffId(Long staffId);

    List<Booking> findByScheduledStartBetween(LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookingStatus(String status);

    List<Booking> findByPetId(Long petId);
}

