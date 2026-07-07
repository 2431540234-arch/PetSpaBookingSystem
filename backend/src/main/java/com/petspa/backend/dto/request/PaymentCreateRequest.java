package com.petspa.backend.dto.request;

import com.petspa.backend.enums.PaymentGateway;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Yêu cầu tạo thanh toán")
public class PaymentCreateRequest {

    @NotNull(message = "ID đặt lịch không được để trống")
    @Schema(description = "ID của Booking cần thanh toán", example = "101")
    private Long bookingId;

    @NotNull(message = "Cổng thanh toán không được để trống")
    @Schema(description = "Cổng thanh toán sử dụng", example = "MOMO")
    private PaymentGateway gateway;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public PaymentGateway getGateway() {
        return gateway;
    }

    public void setGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }
}
