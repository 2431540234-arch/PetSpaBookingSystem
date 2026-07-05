package com.petspa.backend.controller;

import com.petspa.backend.dto.request.BookingRequest;
import com.petspa.backend.dto.response.BookingResponse;
import com.petspa.backend.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request, Authentication authentication) {
        Long customerId = Long.valueOf(authentication.getName());
        return bookingService.createBooking(request, customerId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBookingById(@PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @GetMapping
    public List<BookingResponse> getBookings(Authentication authentication,
                                              @RequestParam(required = false) Long staffId,
                                              @RequestParam(required = false) String date) {
        Long customerId = Long.valueOf(authentication.getName());

        if (staffId != null) {
            return bookingService.getBookingsByStaffId(staffId);
        }
        if (date != null && !date.isEmpty()) {
            return bookingService.getBookingsByDate(date);
        }
        return bookingService.getBookingsByCustomerId(customerId);
    }

    @PutMapping("/{bookingId}")
    public BookingResponse updateBooking(@PathVariable Long bookingId,
                                         @Valid @RequestBody BookingRequest request) {
        return bookingService.updateBooking(bookingId, request);
    }

    @PutMapping("/{bookingId}/status")
    public BookingResponse updateBookingStatus(@PathVariable Long bookingId,
                                               @RequestBody Map<String, String> request) {
        String status = request.get("status");
        return bookingService.updateBookingStatus(bookingId, status);
    }

    @PutMapping("/{bookingId}/payment")
    public BookingResponse updatePayment(@PathVariable Long bookingId,
                                         @RequestBody Map<String, Object> request) {
        String paymentStatus = (String) request.get("paymentStatus");
        java.math.BigDecimal paidAmount = null;
        if (request.get("paidAmount") != null) {
            paidAmount = new java.math.BigDecimal(request.get("paidAmount").toString());
        }
        String transactionId = (String) request.get("transactionId");
        return bookingService.updatePaymentStatus(bookingId, paymentStatus, paidAmount, transactionId);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable Long bookingId) {
        bookingService.deleteBooking(bookingId);
    }
}

