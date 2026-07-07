package com.petspa.backend.exception.payment;

public class GatewayTimeoutException extends GatewayException {
    public GatewayTimeoutException(String gateway) {
        super("Kết nối tới cổng thanh toán " + gateway + " bị quá hạn.");
    }
}
