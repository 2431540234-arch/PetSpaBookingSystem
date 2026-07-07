package com.petspa.backend.payment.factory;

import com.petspa.backend.payment.gateway.PaymentGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentGatewayFactory {

    private final Map<com.petspa.backend.enums.PaymentGateway, PaymentGateway> gatewayMap;

    public PaymentGatewayFactory(List<PaymentGateway> gateways) {
        this.gatewayMap = gateways.stream()
                .collect(Collectors.toMap(PaymentGateway::getProviderType, Function.identity()));
    }

    public PaymentGateway getGateway(com.petspa.backend.enums.PaymentGateway type) {
        PaymentGateway gateway = gatewayMap.get(type);
        if (gateway == null) {
            throw new IllegalArgumentException("Unsupported payment gateway: " + type);
        }
        return gateway;
    }
}
