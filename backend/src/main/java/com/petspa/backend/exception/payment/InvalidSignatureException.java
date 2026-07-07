package com.petspa.backend.exception.payment;

public class InvalidSignatureException extends PaymentException {
    public InvalidSignatureException() {
        super("Invalid payment signature received from gateway.");
    }
}
