package com.petspa.backend.exception.payment;

public class GatewayConnectionException extends GatewayException {
    public GatewayConnectionException(String gateway, String message) {
        super("Không thể kết nối tới cổng thanh toán " + gateway + ": " + message);
    }
}
