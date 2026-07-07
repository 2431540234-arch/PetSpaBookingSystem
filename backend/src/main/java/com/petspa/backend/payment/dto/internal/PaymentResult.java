package com.petspa.backend.payment.dto.internal;

import com.petspa.backend.enums.PaymentStatus;

public class PaymentResult {
    private boolean success;
    private String transactionId;
    private String requestId;
    private PaymentStatus status;
    private String message;

    public PaymentResult(boolean success, String transactionId, String requestId, PaymentStatus status, String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.requestId = requestId;
        this.status = status;
        this.message = message;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
