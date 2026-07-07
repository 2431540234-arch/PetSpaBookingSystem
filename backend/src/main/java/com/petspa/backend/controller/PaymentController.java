package com.petspa.backend.controller;

import com.petspa.backend.dto.request.PaymentCreateRequest;
import com.petspa.backend.entity.PaymentTransaction;
import com.petspa.backend.enums.PaymentGateway;
import com.petspa.backend.payment.dto.internal.PaymentInternalRequest;
import com.petspa.backend.payment.dto.internal.PaymentResponse;
import com.petspa.backend.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment API", description = "Quản lý thanh toán cho dịch vụ PetSpa")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Tạo thanh toán mới", description = "Tạo link thanh toán thông qua cổng MoMo/VNPay/ZaloPay. Yêu cầu JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "403", description = "Không có quyền thanh toán cho booking này"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy Booking")
    })
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentCreateRequest request,
            Authentication authentication) {
        
        String userId = authentication.getName(); // Lấy ID người dùng từ JWT
        
        PaymentInternalRequest internalRequest = new PaymentInternalRequest();
        internalRequest.setBookingId(request.getBookingId());
        internalRequest.setGateway(request.getGateway());
        
        // Return URL và Notify URL có thể được cấu hình ở level service hoặc lấy từ config
        
        PaymentResponse response = paymentService.createPayment(internalRequest, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xử lý Webhook từ cổng thanh toán", description = "Endpoint công khai để nhận thông báo kết quả từ Gateway.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đã nhận thông báo"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu webhook không hợp lệ")
    })
    @PostMapping("/webhook/{gateway}")
    public ResponseEntity<Void> handleWebhook(
            @Parameter(description = "Tên cổng thanh toán", example = "MOMO") @PathVariable String gateway,
            @RequestBody Map<String, String> payload) {
        
        PaymentGateway paymentGateway = PaymentGateway.valueOf(gateway.toUpperCase());
        paymentService.handleWebhook(paymentGateway, payload);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Kiểm tra trạng thái thanh toán", description = "Lấy thông tin giao dịch mới nhất của một Booking. Yêu cầu JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = PaymentTransaction.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch")
    })
    @GetMapping("/{bookingId}/status")
    public ResponseEntity<PaymentTransaction> getStatus(
            @Parameter(description = "ID của Booking", example = "101") @PathVariable Long bookingId) {
        
        PaymentTransaction transaction = paymentService.getLatestPaymentStatus(bookingId);
        return ResponseEntity.ok(transaction);
    }

    @Operation(summary = "Đối soát thủ công", description = "Admin đối soát lại các giao dịch lệch. Yêu cầu quyền ADMIN.")
    @PostMapping("/reconcile")
    public ResponseEntity<Map<String, Object>> reconcile() {
        // Logic đối soát thủ công
        paymentService.dailyReconciliation();
        return ResponseEntity.ok(Map.of("message", "Reconciliation completed", "timestamp", java.time.LocalDateTime.now()));
    }

    @Operation(summary = "Lấy thống kê thanh toán", description = "Lấy các chỉ số metrics thanh toán. Admin only.")
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        return ResponseEntity.ok(paymentService.getMetrics());
    }
}
