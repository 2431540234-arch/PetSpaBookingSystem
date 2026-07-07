package com.petspa.backend.entity;

import com.petspa.backend.enums.PaymentGateway;
import com.petspa.backend.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions", indexes = {
    @Index(name = "idx_request_id", columnList = "request_id"),
    @Index(name = "idx_booking_id", columnList = "booking_id")
})
@Schema(description = "Thông tin giao dịch thanh toán")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID giao dịch nội bộ")
    private Long id;

    @Column(name = "booking_id", nullable = false)
    @Schema(description = "ID của Booking")
    private Long bookingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_gateway", nullable = false, length = 20)
    @Schema(description = "Cổng thanh toán")
    private PaymentGateway paymentGateway;

    @Column(name = "transaction_id", length = 100)
    @Schema(description = "Mã giao dịch từ phía Gateway")
    private String transactionId;

    @Column(name = "request_id", nullable = false, unique = true, length = 100)
    @Schema(description = "Mã yêu cầu gửi đi")
    private String requestId;

    @Column(nullable = false, precision = 19, scale = 2)
    @Schema(description = "Số tiền thanh toán")
    private BigDecimal amount;

    @Column(length = 10)
    @Schema(description = "Tiền tệ", example = "VND")
    private String currency = "VND";

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Schema(description = "Trạng thái thanh toán (PENDING, SUCCESS, FAILED, CANCELLED)")
    private PaymentStatus paymentStatus;

    @Column(columnDefinition = "TEXT")
    private String signature;

    @Column(name = "pay_url", columnDefinition = "TEXT")
    private String payUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currency == null) currency = "VND";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public PaymentGateway getPaymentGateway() { return paymentGateway; }
    public void setPaymentGateway(PaymentGateway paymentGateway) { this.paymentGateway = paymentGateway; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public String getPayUrl() { return payUrl; }
    public void setPayUrl(String payUrl) { this.payUrl = payUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
