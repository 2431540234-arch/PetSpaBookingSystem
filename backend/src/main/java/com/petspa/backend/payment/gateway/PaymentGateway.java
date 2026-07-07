package com.petspa.backend.payment.gateway;

import com.petspa.backend.payment.dto.internal.PaymentInternalRequest;
import com.petspa.backend.payment.dto.internal.PaymentResponse;
import com.petspa.backend.payment.dto.internal.PaymentResult;
import java.util.Map;

public interface PaymentGateway {
    
    com.petspa.backend.enums.PaymentGateway getProviderType();
    
    PaymentResponse createPayment(PaymentInternalRequest request);

    PaymentResult processWebhook(Map<String, String> params);
}
