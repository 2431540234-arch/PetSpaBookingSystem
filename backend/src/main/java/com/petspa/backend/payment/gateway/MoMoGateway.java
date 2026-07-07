package com.petspa.backend.payment.gateway;

import com.petspa.backend.enums.PaymentGateway;
import com.petspa.backend.enums.PaymentStatus;
import com.petspa.backend.exception.payment.GatewayConnectionException;
import com.petspa.backend.exception.payment.InvalidGatewayResponseException;
import com.petspa.backend.exception.payment.SignatureVerificationException;
import com.petspa.backend.payment.config.MoMoConfig;
import com.petspa.backend.payment.dto.gateway.MoMoDTOs;
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
public class MoMoGateway implements com.petspa.backend.payment.gateway.PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(MoMoGateway.class);
    private final MoMoConfig config;
    private final WebClient webClient;

    public MoMoGateway(MoMoConfig config, WebClient paymentWebClient) {
        this.config = config;
        this.webClient = paymentWebClient;
    }

    @Override
    public PaymentGateway getProviderType() {
        return PaymentGateway.MOMO;
    }

    @Override
    public PaymentResponse createPayment(PaymentInternalRequest request) {
        log.info("[MoMo] Creating payment for booking: {}", request.getBookingId());
        
        String requestId = request.getRequestId();
        String orderId = request.getRequestId(); // Using requestId as orderId for simplicity
        long amount = request.getAmount().longValue();
        String orderInfo = "Thanh toán PetSpa cho đơn hàng #" + request.getBookingId();
        String requestType = "captureWallet";
        String extraData = "";

        try {
            String rawHash = "accessKey=" + config.getAccessKey() +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + config.getIpnUrl() +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + config.getPartnerCode() +
                    "&redirectUrl=" + config.getRedirectUrl() +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = PaymentUtils.hmacSha256(rawHash, config.getSecretKey());

            MoMoDTOs.MoMoRequest moMoRequest = new MoMoDTOs.MoMoRequest();
            moMoRequest.partnerCode = config.getPartnerCode();
            moMoRequest.requestId = requestId;
            moMoRequest.amount = amount;
            moMoRequest.orderId = orderId;
            moMoRequest.orderInfo = orderInfo;
            moMoRequest.redirectUrl = config.getRedirectUrl();
            moMoRequest.ipnUrl = config.getIpnUrl();
            moMoRequest.requestType = requestType;
            moMoRequest.signature = signature;

            log.info("[MoMo] Requesting gateway: {}", config.getEndpoint());
            
            MoMoDTOs.MoMoResponse response = webClient.post()
                    .uri(config.getEndpoint())
                    .bodyValue(moMoRequest)
                    .retrieve()
                    .bodyToMono(MoMoDTOs.MoMoResponse.class)
                    .retryWhen(Retry.from(companion -> companion.flatMap(signal -> {
                        long retryCount = signal.totalRetries();
                        if (retryCount >= 2) {
                            return reactor.core.publisher.Mono.error(signal.failure());
                        }
                        long delay = (retryCount == 0) ? 2 : 5;
                        log.info("[MoMo] Gateway timeout. Retrying in {}s (Attempt {})", delay, retryCount + 2);
                        return reactor.core.publisher.Mono.delay(Duration.ofSeconds(delay));
                    })))
                    .block();

            if (response == null || response.payUrl == null) {
                log.error("[MoMo] Null response or missing payUrl. ResultCode: {}", response != null ? response.resultCode : "N/A");
                throw new InvalidGatewayResponseException("MOMO", "Không nhận được payUrl từ MoMo");
            }

            log.info("[MoMo] Payment link created: {}", response.payUrl);
            return new PaymentResponse(response.payUrl, requestId, response.message);

        } catch (Exception e) {
            log.error("[MoMo] Error creating payment", e);
            throw new GatewayConnectionException("MOMO", e.getMessage());
        }
    }

    @Override
    public PaymentResult processWebhook(Map<String, String> params) {
        log.info("[MoMo] Processing webhook: {}", params);
        
        String signature = params.get("signature");
        // Re-calculate signature to verify
        // MoMo IPN signature components: accessKey, amount, extraData, message, orderId, orderInfo, orderType, partnerCode, requestId, responseTime, resultCode, transId
        try {
            String rawHash = "accessKey=" + config.getAccessKey() +
                    "&amount=" + params.get("amount") +
                    "&extraData=" + params.getOrDefault("extraData", "") +
                    "&message=" + params.get("message") +
                    "&orderId=" + params.get("orderId") +
                    "&orderInfo=" + params.get("orderInfo") +
                    "&orderType=" + params.get("orderType") +
                    "&partnerCode=" + params.get("partnerCode") +
                    "&requestId=" + params.get("requestId") +
                    "&responseTime=" + params.get("responseTime") +
                    "&resultCode=" + params.get("resultCode") +
                    "&transId=" + params.get("transId");

            String calculatedSignature = PaymentUtils.hmacSha256(rawHash, config.getSecretKey());

            if (!calculatedSignature.equals(signature)) {
                log.warn("[MoMo] Signature mismatch! Calculated: {}, Received: {}", calculatedSignature, signature);
                throw new SignatureVerificationException("MOMO");
            }

            int resultCode = Integer.parseInt(params.get("resultCode"));
            PaymentStatus status = (resultCode == 0) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
            
            log.info("[MoMo] Webhook verified. Status: {}", status);
            return new PaymentResult(true, params.get("transId"), params.get("requestId"), status, params.get("message"));

        } catch (Exception e) {
            log.error("[MoMo] Webhook verification error", e);
            throw new SignatureVerificationException("MOMO");
        }
    }
}
