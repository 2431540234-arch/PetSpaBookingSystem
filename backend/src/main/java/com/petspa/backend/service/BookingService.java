package com.petspa.backend.service;

import com.petspa.backend.dto.request.BookingRequest;
import com.petspa.backend.dto.response.BookingResponse;
import com.petspa.backend.entity.Booking;
import com.petspa.backend.entity.ServiceCatalog;
import com.petspa.backend.exception.ResourceNotFoundException;
import com.petspa.backend.repository.BookingRepository;
import com.petspa.backend.repository.ServiceCatalogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceCatalogRepository serviceCatalogRepository;
    private final NotificationService notificationService;

    public BookingService(BookingRepository bookingRepository,
                         ServiceCatalogRepository serviceCatalogRepository,
                         NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.serviceCatalogRepository = serviceCatalogRepository;
        this.notificationService = notificationService;
    }

    public BookingResponse createBooking(BookingRequest request, Long customerId) {
        // Lấy thông tin dịch vụ để tính tổng tiền
        ServiceCatalog service = serviceCatalogRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        Booking booking = new Booking();
        booking.setPetId(request.getPetId());
        booking.setServiceId(request.getServiceId());
        booking.setStaffId(request.getStaffId());
        booking.setCustomerId(customerId);

        // Parse date/time into scheduledStart and scheduledEnd
        java.time.LocalDate date = java.time.LocalDate.parse(request.getDate());
        java.time.LocalTime time = java.time.LocalTime.parse(request.getTime());
        java.time.LocalDateTime start = java.time.LocalDateTime.of(date, time);
        int duration = request.getServiceDuration() != null ? request.getServiceDuration() : service.getDuration();
        java.time.LocalDateTime end = start.plusMinutes(duration);
        booking.setScheduledStart(start);
        booking.setScheduledEnd(end);

        booking.setNotes(request.getNotes());
        booking.setCustomerRequests(request.getCustomerRequests());
        booking.setServiceDuration(duration);
        booking.setTotalAmount(service.getPrice());
        booking.setBookingStatus("PENDING");
        booking.setPaymentStatus("UNPAID");

        Booking savedBooking = bookingRepository.save(booking);

        // Gửi notification (convert ids to String for notification service until refactor)
        notificationService.notifyBookingCreated(String.valueOf(customerId), String.valueOf(savedBooking.getId()));

        return convertToResponse(savedBooking);
    }

    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        return convertToResponse(booking);
    }

    public List<BookingResponse> getBookingsByCustomerId(Long customerId) {
        List<Booking> bookings = bookingRepository.findByCustomerId(customerId);
        return bookings.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByStaffId(Long staffId) {
        List<Booking> bookings = bookingRepository.findByStaffId(staffId);
        return bookings.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByDate(String date) {
        java.time.LocalDate d = java.time.LocalDate.parse(date);
        java.time.LocalDateTime start = d.atStartOfDay();
        java.time.LocalDateTime end = d.atTime(23, 59, 59);
        List<Booking> bookings = bookingRepository.findByScheduledStartBetween(start, end);
        return bookings.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public BookingResponse updateBooking(Long bookingId, BookingRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (request.getDate() != null && request.getTime() != null) {
            java.time.LocalDate date = java.time.LocalDate.parse(request.getDate());
            java.time.LocalTime time = java.time.LocalTime.parse(request.getTime());
            java.time.LocalDateTime start = java.time.LocalDateTime.of(date, time);
            int duration = request.getServiceDuration() != null ? request.getServiceDuration() : booking.getServiceDuration();
            booking.setScheduledStart(start);
            booking.setScheduledEnd(start.plusMinutes(duration));
        }
        if (request.getNotes() != null) booking.setNotes(request.getNotes());
        if (request.getCustomerRequests() != null) booking.setCustomerRequests(request.getCustomerRequests());
        if (request.getStaffId() != null) booking.setStaffId(request.getStaffId());

        Booking updatedBooking = bookingRepository.save(booking);
        return convertToResponse(updatedBooking);
    }

    public BookingResponse updateBookingStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        booking.setBookingStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);

        // Gửi notification theo trạng thái (convert ids to String)
        if ("CONFIRMED".equalsIgnoreCase(status)) {
            notificationService.notifyBookingConfirmed(String.valueOf(booking.getCustomerId()), String.valueOf(bookingId));
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            notificationService.notifyServiceCompleted(String.valueOf(booking.getCustomerId()), String.valueOf(bookingId));
        }

        return convertToResponse(updatedBooking);
    }

    public BookingResponse updatePaymentStatus(Long bookingId, String paymentStatus, java.math.BigDecimal paidAmount, String transactionId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        booking.setPaymentStatus(paymentStatus);
        booking.setPaidAmount(paidAmount);
        booking.setTransactionId(transactionId);

        Booking updatedBooking = bookingRepository.save(booking);

        if ("SUCCESS".equalsIgnoreCase(paymentStatus) || "FULLY_PAID".equalsIgnoreCase(paymentStatus)) {
            notificationService.notifyPaymentSuccess(String.valueOf(booking.getCustomerId()), String.valueOf(bookingId));
        }

        return convertToResponse(updatedBooking);
    }

    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        bookingRepository.delete(booking);
    }

    private BookingResponse convertToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setPetId(booking.getPetId());
        response.setServiceId(booking.getServiceId());
        response.setStaffId(booking.getStaffId());
        response.setCustomerId(booking.getCustomerId());
        response.setScheduledStart(booking.getScheduledStart());
        response.setScheduledEnd(booking.getScheduledEnd());
        response.setNotes(booking.getNotes());
        response.setStatus(booking.getBookingStatus());
        response.setPaymentStatus(booking.getPaymentStatus());
        response.setTotalAmount(booking.getTotalAmount());
        response.setPaidAmount(booking.getPaidAmount());
        response.setPaymentMethod(booking.getPaymentMethod());
        response.setTransactionId(booking.getTransactionId());
        response.setCreatedAt(booking.getCreatedAt());
        response.setServiceDuration(booking.getServiceDuration());
        response.setCustomerRequests(booking.getCustomerRequests());
        return response;
    }
}

