package com.petspa.backend.payment.dto.gateway;

import java.util.Map;

public class VNPayDTOs {
    public static class VNPayRequest {
        public Map<String, String> vnpParams;
    }

    public static class VNPayResponse {
        public String paymentUrl;
    }
}
