package com.petspa.backend.exception.payment;

public class InvalidGatewayResponseException extends GatewayException {
    public InvalidGatewayResponseException(String gateway, String message) {
        super("Phản hồi từ cổng " + gateway + " không hợp lệ: " + message);
    }
}
