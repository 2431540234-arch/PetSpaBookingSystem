package com.petspa.backend.payment.dto.gateway;

public class MoMoDTOs {
    public static class MoMoRequest {
        public String partnerCode;
        public String requestId;
        public Long amount;
        public String orderId;
        public String orderInfo;
        public String redirectUrl;
        public String ipnUrl;
        public String requestType;
        public String signature;
    }

    public static class MoMoResponse {
        public String payUrl;
        public String qrCodeUrl;
        public int resultCode;
        public String message;
    }
}
