package com.petspa.backend.exception.payment;

import com.petspa.backend.enums.PaymentGateway;

public class UnsupportedGatewayException extends PaymentException {
    public UnsupportedGatewayException(PaymentGateway gateway) {
        super("Payment gateway " + gateway + " is not supported.");
    }
}
