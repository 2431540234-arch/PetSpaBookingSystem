package com.petspa.backend.repository;

import com.petspa.backend.entity.PaymentTransaction;
import com.petspa.backend.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByRequestId(String requestId);

    @Query("SELECT p FROM PaymentTransaction p WHERE p.bookingId = :bookingId ORDER BY p.createdAt DESC LIMIT 1")
    Optional<PaymentTransaction> findLatestByBookingId(Long bookingId);

    boolean existsByRequestId(String requestId);

    boolean existsByTransactionId(String transactionId);

    @Query("SELECT p FROM PaymentTransaction p WHERE p.paymentStatus = 'PENDING' AND p.createdAt < :cutoff")
    List<PaymentTransaction> findExpiredPendingTransactions(LocalDateTime cutoff);

    @Query("SELECT p FROM PaymentTransaction p WHERE p.paymentStatus = 'PENDING'")
    List<PaymentTransaction> findPendingTransactions();

    @Query("SELECT p FROM PaymentTransaction p WHERE p.paymentStatus = 'SUCCESS'")
    List<PaymentTransaction> findSuccessfulTransactions();
    
    List<PaymentTransaction> findAllByBookingId(Long bookingId);
}
