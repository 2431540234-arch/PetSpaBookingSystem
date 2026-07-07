package com.petspa.backend.exception.payment;

public class BookingAlreadyPaidException extends PaymentException {
    public BookingAlreadyPaidException(Long bookingId) {
        super("Booking with ID " + bookingId + " is already paid.");
    }
}
