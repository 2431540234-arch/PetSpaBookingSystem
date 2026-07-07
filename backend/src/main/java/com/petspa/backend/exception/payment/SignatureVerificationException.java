package com.petspa.backend.exception.payment;

public class SignatureVerificationException extends GatewayException {
    public SignatureVerificationException(String gateway) {
        super("Xác minh chữ ký từ cổng " + gateway + " thất bại.");
    }
}
