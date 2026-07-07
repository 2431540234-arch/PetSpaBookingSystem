package com.petspa.backend.payment.gateway;

import com.petspa.backend.enums.PaymentGateway;
import com.petspa.backend.enums.PaymentStatus;
import com.petspa.backend.exception.payment.SignatureVerificationException;
import com.petspa.backend.payment.config.VNPayConfig;
import com.petspa.backend.payment.dto.internal.PaymentInternalRequest;
import com.petspa.backend.payment.dto.internal.PaymentResponse;
import com.petspa.backend.payment.dto.internal.PaymentResult;
import com.petspa.backend.payment.factory.PaymentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class VNPayGateway implements com.petspa.backend.payment.gateway.PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(VNPayGateway.class);
    private final VNPayConfig config;

    public VNPayGateway(VNPayConfig config) {
        this.config = config;
    }

    @Override
    public PaymentGateway getProviderType() {
        return PaymentGateway.VNPAY;
    }

    @Override
    public PaymentResponse createPayment(PaymentInternalRequest request) {
        log.info("[VNPay] Creating payment for booking: {}", request.getBookingId());

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = request.getRequestId();
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = config.getTmnCode();
        String vnp_OrderType = "other";
        
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(request.getAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue()));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan PetSpa #" + request.getBookingId());
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", config.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", now.format(formatter));

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        
        String queryUrl = query.toString();
        try {
            String vnp_SecureHash = PaymentUtils.hmacSha512(hashData.toString(), config.getHashSecret());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = config.getEndpoint() + "?" + queryUrl;
            
            log.info("[VNPay] Payment link generated: {}", paymentUrl);
            return new PaymentResponse(paymentUrl, vnp_TxnRef, "Success");
        } catch (Exception e) {
            log.error("[VNPay] Error creating payment", e);
            throw new RuntimeException("VNPay error: " + e.getMessage());
        }
    }

    @Override
    public PaymentResult processWebhook(Map<String, String> params) {
        log.info("[VNPay] Processing webhook: {}", params);
        
        String vnp_SecureHash = params.get("vnp_SecureHash");
        Map<String, String> vnp_Params = new HashMap<>(params);
        vnp_Params.remove("vnp_SecureHash");
        vnp_Params.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }

        try {
            String calculatedHash = PaymentUtils.hmacSha512(hashData.toString(), config.getHashSecret());
            if (!calculatedHash.equalsIgnoreCase(vnp_SecureHash)) {
                log.warn("[VNPay] Signature mismatch! Calculated: {}, Received: {}", calculatedHash, vnp_SecureHash);
                throw new SignatureVerificationException("VNPAY");
            }

            String responseCode = params.get("vnp_ResponseCode");
            PaymentStatus status = "00".equals(responseCode) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
            
            log.info("[VNPay] Webhook verified. Status: {}", status);
            return new PaymentResult(true, params.get("vnp_TransactionNo"), params.get("vnp_TxnRef"), status, "VNPay response: " + responseCode);
            
        } catch (Exception e) {
            log.error("[VNPay] Webhook verification error", e);
            throw new SignatureVerificationException("VNPAY");
        }
    }
}
