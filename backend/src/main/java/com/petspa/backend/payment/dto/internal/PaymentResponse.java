package com.petspa.backend.payment.dto.internal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Thông tin phản hồi khi tạo thanh toán")
public class PaymentResponse {
    
    @Schema(description = "Đường dẫn chuyển hướng đến trang thanh toán", example = "https://momo.vn/pay/...")
    private String payUrl;
    
    @Schema(description = "Mã yêu cầu duy nhất", example = "uuid-1234")
    private String requestId;
    
    @Schema(description = "Thông báo từ hệ thống", example = "Success")
    private String message;

    public PaymentResponse(String payUrl, String requestId, String message) {
        this.payUrl = payUrl;
        this.requestId = requestId;
        this.message = message;
    }

    // Getters and Setters
    public String getPayUrl() { return payUrl; }
    public void setPayUrl(String payUrl) { this.payUrl = payUrl; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
