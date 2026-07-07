package com.petspa.backend.payment;

import com.petspa.backend.entity.Booking;
import com.petspa.backend.entity.PaymentTransaction;
import com.petspa.backend.enums.PaymentGateway;
import com.petspa.backend.enums.PaymentStatus;
import com.petspa.backend.payment.dto.internal.PaymentInternalRequest;
import com.petspa.backend.payment.dto.internal.PaymentResponse;
import com.petspa.backend.payment.factory.PaymentGatewayFactory;
import com.petspa.backend.payment.service.PaymentService;
import com.petspa.backend.repository.BookingRepository;
import com.petspa.backend.repository.PaymentAuditLogRepository;
import com.petspa.backend.repository.PaymentTransactionRepository;
import com.petspa.backend.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private PaymentService paymentService;

    @Mock private PaymentTransactionRepository transactionRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private PaymentGatewayFactory gatewayFactory;
    @Mock private NotificationService notificationService;
    @Mock private PaymentAuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentService(
                transactionRepository,
                bookingRepository,
                gatewayFactory,
                notificationService,
                auditLogRepository
        );
    }

    @Test
    void createPayment_Success() {
        // Arrange
        Long bookingId = 1L;
        String userId = "10";
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setCustomerId(10L);
        booking.setTotalAmount(BigDecimal.valueOf(100000));
        booking.setPaymentStatus("UNPAID");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(transactionRepository.save(any())).thenReturn(new PaymentTransaction());
        
        com.petspa.backend.payment.gateway.PaymentGateway gateway = mock(com.petspa.backend.payment.gateway.PaymentGateway.class);
        when(gatewayFactory.getGateway(any())).thenReturn(gateway);
        when(gateway.createPayment(any())).thenReturn(new PaymentResponse("http://pay.url", "req-123", "OK"));

        PaymentInternalRequest request = new PaymentInternalRequest();
        request.setBookingId(bookingId);
        request.setGateway(PaymentGateway.MOMO);

        // Act
        PaymentResponse response = paymentService.createPayment(request, userId);

        // Assert
        assertNotNull(response);
        assertEquals("http://pay.url", response.getPayUrl());
        verify(transactionRepository, times(2)).save(any());
        verify(auditLogRepository, atLeastOnce()).save(any());
    }

    @Test
    void handleWebhook_Success() {
        // Arrange
        String requestId = "req-123";
        PaymentTransaction tx = new PaymentTransaction();
        tx.setRequestId(requestId);
        tx.setBookingId(1L);
        tx.setAmount(BigDecimal.valueOf(100000));
        tx.setPaymentStatus(PaymentStatus.PENDING);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCustomerId(10L);

        when(transactionRepository.findByRequestId(requestId)).thenReturn(Optional.of(tx));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        
        com.petspa.backend.payment.gateway.PaymentGateway gateway = mock(com.petspa.backend.payment.gateway.PaymentGateway.class);
        when(gatewayFactory.getGateway(any())).thenReturn(gateway);
        
        com.petspa.backend.payment.dto.internal.PaymentResult result = 
            new com.petspa.backend.payment.dto.internal.PaymentResult(true, "trans-456", requestId, PaymentStatus.SUCCESS, "Success");
        when(gateway.processWebhook(any())).thenReturn(result);

        // Act
        paymentService.handleWebhook(PaymentGateway.MOMO, java.util.Map.of("requestId", requestId));

        // Assert
        assertEquals(PaymentStatus.SUCCESS, tx.getPaymentStatus());
        assertEquals("FULLY_PAID", booking.getPaymentStatus());
        verify(notificationService).notifyPaymentSuccess(anyString(), anyString());
    }
}
