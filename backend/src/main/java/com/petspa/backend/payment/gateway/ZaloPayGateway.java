package com.petspa.backend.payment.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petspa.backend.enums.PaymentGateway;
import com.petspa.backend.enums.PaymentStatus;
import com.petspa.backend.exception.payment.GatewayConnectionException;
import com.petspa.backend.exception.payment.InvalidGatewayResponseException;
import com.petspa.backend.exception.payment.SignatureVerificationException;
import com.petspa.backend.payment.config.ZaloPayConfig;
import com.petspa.backend.payment.dto.gateway.ZaloPayDTOs;
import com.petspa.backend.payment.dto.internal.PaymentInternalRequest;
import com.petspa.backend.payment.dto.internal.PaymentResponse;
import com.petspa.backend.payment.dto.internal.PaymentResult;
import com.petspa.backend.payment.factory.PaymentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Component
public class ZaloPayGateway implements com.petspa.backend.payment.gateway.PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(ZaloPayGateway.class);
    private final ZaloPayConfig config;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ZaloPayGateway(ZaloPayConfig config, WebClient paymentWebClient, ObjectMapper objectMapper) {
        this.config = config;
        this.webClient = paymentWebClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public PaymentGateway getProviderType() {
        return PaymentGateway.ZALOPAY;
    }

    @Override
    public PaymentResponse createPayment(PaymentInternalRequest request) {
        log.info("[ZaloPay] Creating payment for booking: {}", request.getBookingId());

        long amount = request.getAmount().longValue();
        String appTransId = request.getRequestId(); 
        long appTime = System.currentTimeMillis();
        String appUser = "petspa_user";
        String embedData = "{}";
        String item = "[]";
        String description = "Thanh toán PetSpa #" + request.getBookingId();

        try {
            String data = config.getAppId() + "|" + appTransId + "|" + appUser + "|" + amount + "|" + appTime + "|" + embedData + "|" + item;
            String mac = PaymentUtils.hmacSha256(data, config.getKey1());

            ZaloPayDTOs.ZaloRequest zaloRequest = new ZaloPayDTOs.ZaloRequest();
            zaloRequest.appId = config.getAppId();
            zaloRequest.appTransId = appTransId;
            zaloRequest.appTime = appTime;
            zaloRequest.appUser = appUser;
            zaloRequest.amount = amount;
            zaloRequest.description = description;
            zaloRequest.embedData = embedData;
            zaloRequest.item = item;
            zaloRequest.mac = mac;

            ZaloPayDTOs.ZaloResponse response = webClient.post()
                    .uri(config.getEndpoint())
                    .bodyValue(zaloRequest)
                    .retrieve()
                    .bodyToMono(ZaloPayDTOs.ZaloResponse.class)
                    .retryWhen(Retry.from(companion -> companion.flatMap(signal -> {
                        long retryCount = signal.totalRetries();
                        if (retryCount >= 2) {
                            return reactor.core.publisher.Mono.error(signal.failure());
                        }
                        long delay = (retryCount == 0) ? 2 : 5;
                        log.info("[ZaloPay] Gateway timeout. Retrying in {}s (Attempt {})", delay, retryCount + 2);
                        return reactor.core.publisher.Mono.delay(Duration.ofSeconds(delay));
                    })))
                    .block();

            if (response == null || response.orderUrl == null) {
                log.error("[ZaloPay] Null response or missing orderUrl. ReturnCode: {}", response != null ? response.returnCode : "N/A");
                throw new InvalidGatewayResponseException("ZALOPAY", "Không nhận được orderUrl từ ZaloPay");
            }

            log.info("[ZaloPay] Payment link created: {}", response.orderUrl);
            return new PaymentResponse(response.orderUrl, appTransId, response.returnMessage);

        } catch (Exception e) {
            log.error("[ZaloPay] Error creating payment", e);
            throw new GatewayConnectionException("ZALOPAY", e.getMessage());
        }
    }

    @Override
    public PaymentResult processWebhook(Map<String, String> params) {
        log.info("[ZaloPay] Processing callback: {}", params);
        
        // ZaloPay callback usually contains a JSON 'data' and a 'mac'
        String dataStr = params.get("data");
        String requestMac = params.get("mac");

        try {
            String calculatedMac = PaymentUtils.hmacSha256(dataStr, config.getKey2());
            if (!calculatedMac.equals(requestMac)) {
                log.warn("[ZaloPay] MAC mismatch! Calculated: {}, Received: {}", calculatedMac, requestMac);
                throw new SignatureVerificationException("ZALOPAY");
            }

            // Parse data to get status and transactionId
            JsonNode dataJson = objectMapper.readTree(dataStr);
            String appTransId = dataJson.get("app_trans_id").asText();
            String zpTransId = dataJson.get("zp_trans_id").asText();
            
            log.info("[ZaloPay] Callback verified successfully for appTransId: {}", appTransId);
            return new PaymentResult(true, zpTransId, appTransId, PaymentStatus.SUCCESS, "ZaloPay Verified");

        } catch (Exception e) {
            log.error("[ZaloPay] Callback verification error", e);
            throw new SignatureVerificationException("ZALOPAY");
        }
    }
}
