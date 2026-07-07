package com.petspa.backend.payment.service;

import com.petspa.backend.entity.Booking;
import com.petspa.backend.entity.PaymentAuditLog;
import com.petspa.backend.entity.PaymentTransaction;
import com.petspa.backend.enums.PaymentGateway;
import com.petspa.backend.enums.PaymentStatus;
import com.petspa.backend.exception.ResourceNotFoundException;
import com.petspa.backend.exception.payment.*;
import com.petspa.backend.payment.dto.internal.PaymentInternalRequest;
import com.petspa.backend.payment.dto.internal.PaymentResponse;
import com.petspa.backend.payment.dto.internal.PaymentResult;
import com.petspa.backend.payment.factory.PaymentGatewayFactory;
import com.petspa.backend.repository.BookingRepository;
import com.petspa.backend.repository.PaymentAuditLogRepository;
import com.petspa.backend.repository.PaymentTransactionRepository;
import com.petspa.backend.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentTransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;
    private final PaymentGatewayFactory gatewayFactory;
    private final NotificationService notificationService;
    private final PaymentAuditLogRepository auditLogRepository;

    // Metrics
    private final AtomicLong totalPayments = new AtomicLong(0);
    private final AtomicLong successfulPayments = new AtomicLong(0);
    private final AtomicLong failedPayments = new AtomicLong(0);
    private final AtomicLong timeoutPayments = new AtomicLong(0);

    public PaymentService(PaymentTransactionRepository transactionRepository,
                          BookingRepository bookingRepository,
                          PaymentGatewayFactory gatewayFactory,
                          NotificationService notificationService,
                          PaymentAuditLogRepository auditLogRepository) {
        this.transactionRepository = transactionRepository;
        this.bookingRepository = bookingRepository;
        this.gatewayFactory = gatewayFactory;
        this.notificationService = notificationService;
        this.auditLogRepository = auditLogRepository;
    }

    private void logAudit(Long bookingId, String requestId, String action, String details) {
        PaymentAuditLog auditLog = new PaymentAuditLog();
        auditLog.setBookingId(bookingId);
        auditLog.setRequestId(requestId);
        auditLog.setAction(action);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
        log.info("[Audit] Booking: {}, Action: {}, Details: {}", bookingId, action, details);
    }

    /**
     * Tự động EXPIRED transaction PENDING quá 15 phút.
     * Chạy mỗi 1 phút.
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void expirePendingTransactions() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(15);
        List<PaymentTransaction> pending = transactionRepository.findExpiredPendingTransactions(cutoff);
        
        for (PaymentTransaction tx : pending) {
            tx.setPaymentStatus(PaymentStatus.EXPIRED);
            transactionRepository.save(tx);
            logAudit(tx.getBookingId(), tx.getRequestId(), "EXPIRED", "Transaction expired after 15 minutes PENDING");
        }
    }

    /**
     * Đối soát hàng đêm vào lúc 02:00.
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void dailyReconciliation() {
        log.info("[Reconciliation] Starting daily reconciliation...");
        List<PaymentTransaction> successfulTxs = transactionRepository.findSuccessfulTransactions();
        
        for (PaymentTransaction tx : successfulTxs) {
            Booking booking = bookingRepository.findById(tx.getBookingId()).orElse(null);
            if (booking != null && !"FULLY_PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
                log.warn("[Reconciliation] Inconsistency found! Transaction {} is SUCCESS but Booking {} is {}. Fixing...", 
                        tx.getRequestId(), booking.getId(), booking.getPaymentStatus());
                
                booking.setPaymentStatus("FULLY_PAID");
                booking.setBookingStatus("CONFIRMED");
                booking.setTransactionId(tx.getTransactionId());
                booking.setPaidAmount(tx.getAmount());
                bookingRepository.save(booking);
                
                logAudit(booking.getId(), tx.getRequestId(), "RECONCILIATION_FIX", "Auto-fixed inconsistent payment status");
            }
        }
        log.info("[Reconciliation] Daily reconciliation completed.");
    }

    @Transactional
    public PaymentResponse createPayment(PaymentInternalRequest internalRequest, String currentUserId) {
        totalPayments.incrementAndGet();
        // 1. Validate Booking
        Booking booking = bookingRepository.findById(internalRequest.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + internalRequest.getBookingId()));

        // 2. Security Check (Must belong to current user)
        if (!booking.getCustomerId().toString().equals(currentUserId)) {
            logAudit(internalRequest.getBookingId(), null, "SECURITY_VIOLATION", "User " + currentUserId + " tried to pay for booking owned by " + booking.getCustomerId());
            throw new PaymentException("Unauthorized payment request for this booking.");
        }

        // 3. Status Check
        if ("PAID".equalsIgnoreCase(booking.getPaymentStatus()) || "FULLY_PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            throw new BookingAlreadyPaidException(booking.getId());
        }

        // 4. Generate Request ID and Transaction record
        String requestId = UUID.randomUUID().toString();
        internalRequest.setRequestId(requestId);
        internalRequest.setAmount(booking.getTotalAmount()); // Always use amount from DB

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setBookingId(booking.getId());
        transaction.setPaymentGateway(internalRequest.getGateway());
        transaction.setRequestId(requestId);
        transaction.setAmount(booking.getTotalAmount());
        transaction.setPaymentStatus(PaymentStatus.PENDING);
        
        transactionRepository.save(transaction);
        logAudit(booking.getId(), requestId, "CREATE_PAYMENT", "Initiated " + internalRequest.getGateway() + " payment");

        try {
            // 5. Invoke Gateway
            com.petspa.backend.payment.gateway.PaymentGateway gateway = gatewayFactory.getGateway(internalRequest.getGateway());
            PaymentResponse response = gateway.createPayment(internalRequest);

            // 6. Update Transaction with PayURL
            transaction.setPayUrl(response.getPayUrl());
            transactionRepository.save(transaction);

            return response;
        } catch (GatewayTimeoutException e) {
            timeoutPayments.incrementAndGet();
            logAudit(booking.getId(), requestId, "GATEWAY_TIMEOUT", e.getMessage());
            throw e;
        } catch (Exception e) {
            failedPayments.incrementAndGet();
            logAudit(booking.getId(), requestId, "GATEWAY_ERROR", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void handleWebhook(PaymentGateway gatewayType, Map<String, String> params) {
        // 1. Identify Gateway and Process Webhook
        com.petspa.backend.payment.gateway.PaymentGateway gateway = gatewayFactory.getGateway(gatewayType);
        PaymentResult result = gateway.processWebhook(params);

        String finalRequestId = result.getRequestId();
        if (finalRequestId == null) {
             throw new PaymentNotFoundException("Request ID missing in gateway result");
        }

        logAudit(null, finalRequestId, "WEBHOOK_RECEIVED", "Received webhook from " + gatewayType);

        if (!result.isSuccess()) {
            logAudit(null, finalRequestId, "SIGNATURE_INVALID", "Signature verification failed for " + gatewayType);
            throw new InvalidSignatureException();
        }

        // 2. Idempotency Check & Find Transaction
        PaymentTransaction transaction = transactionRepository.findByRequestId(finalRequestId)
                .orElseThrow(() -> new PaymentNotFoundException("Transaction not found for request ID: " + finalRequestId));

        // Idempotency: If SUCCESS already, don't do anything
        if (transaction.getPaymentStatus() == PaymentStatus.SUCCESS) {
            log.info("[Idempotency] Transaction {} already SUCCESS. Skipping.", finalRequestId);
            return; 
        }

        // 3. Update Transaction
        transaction.setTransactionId(result.getTransactionId());
        transaction.setPaymentStatus(result.getStatus());
        
        if (result.getStatus() == PaymentStatus.SUCCESS) {
            successfulPayments.incrementAndGet();
            transaction.setPaidAt(LocalDateTime.now());
            
            // 4. Update Booking Status
            Booking booking = bookingRepository.findById(transaction.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking lost during payment? ID: " + transaction.getBookingId()));
            
            booking.setPaymentStatus("FULLY_PAID");
            booking.setBookingStatus("CONFIRMED"); // Auto confirm on successful payment
            booking.setTransactionId(result.getTransactionId());
            booking.setPaidAmount(transaction.getAmount());
            
            bookingRepository.save(booking);
            logAudit(booking.getId(), finalRequestId, "PAYMENT_SUCCESS", "Booking " + booking.getId() + " marked as FULLY_PAID");

            notificationService.notifyPaymentSuccess(String.valueOf(booking.getCustomerId()), String.valueOf(booking.getId()));
        } else {
            failedPayments.incrementAndGet();
            logAudit(transaction.getBookingId(), finalRequestId, "PAYMENT_FAILED", "Gateway returned status: " + result.getStatus());

            Booking booking = bookingRepository.findById(transaction.getBookingId()).orElse(null);
            if (booking != null) {
                notificationService.notifyPaymentFailed(String.valueOf(booking.getCustomerId()), String.valueOf(booking.getId()));
            }
        }

        transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public PaymentTransaction getLatestPaymentStatus(Long bookingId) {
        return transactionRepository.findLatestByBookingId(bookingId)
                .orElseThrow(() -> new PaymentNotFoundException("No payment transaction found for booking ID: " + bookingId));
    }

    public Map<String, Object> getMetrics() {
        long total = totalPayments.get();
        long success = successfulPayments.get();
        long failed = failedPayments.get();
        long timeout = timeoutPayments.get();
        
        double successRate = total == 0 ? 0 : (double) success / total * 100;
        
        return Map.of(
            "total_payments", total,
            "success_payments", success,
            "failed_payments", failed,
            "timeout_payments", timeout,
            "success_rate", String.format("%.2f%%", successRate)
        );
    }
}
