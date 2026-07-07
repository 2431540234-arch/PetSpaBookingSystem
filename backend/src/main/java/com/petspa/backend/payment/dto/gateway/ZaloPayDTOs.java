package com.petspa.backend.payment.dto.gateway;

public class ZaloPayDTOs {
    public static class ZaloRequest {
        public String appId;
        public String appTransId;
        public Long appTime;
        public String appUser;
        public Long amount;
        public String description;
        public String embedData;
        public String item;
        public String mac;
    }

    public static class ZaloResponse {
        public int returnCode;
        public String returnMessage;
        public String orderUrl;
    }
}
