package com.petspa.backend.exception.payment;

public class GatewayException extends PaymentException {
    public GatewayException(String message) {
        super(message);
    }
}
