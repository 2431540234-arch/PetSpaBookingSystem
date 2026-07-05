package com.petspa.app.data.local.mapper

import com.petspa.app.data.local.entity.BookingEntity
import com.petspa.app.model.Booking

fun BookingEntity.toBooking(): Booking {
    return Booking(
        id = id,
        petId = petId,
        petName = petName,
        serviceId = serviceId,
        serviceName = serviceName,
        staffId = staffId,
        staffName = staffName,
        customerId = customerId,
        customerName = customerName,
        customerPhone = customerPhone,
        date = date,
        time = time,
        notes = notes,
        status = status,
        paymentStatus = paymentStatus,
        totalAmount = totalAmount,
        paidAmount = paidAmount,
        paymentMethod = paymentMethod,
        transactionId = transactionId,
        createdAt = createdAt,
        serviceDuration = serviceDuration,
        customerRequests = customerRequests
    )
}

fun Booking.toEntity(): BookingEntity {
    return BookingEntity(
        id = id,
        petId = petId,
        petName = petName,
        serviceId = serviceId,
        serviceName = serviceName,
        staffId = staffId,
        staffName = staffName,
        customerId = customerId,
        customerName = customerName,
        customerPhone = customerPhone,
        date = date,
        time = time,
        notes = notes,
        status = status,
        paymentStatus = paymentStatus,
        totalAmount = totalAmount,
        paidAmount = paidAmount,
        paymentMethod = paymentMethod,
        transactionId = transactionId,
        createdAt = createdAt,
        serviceDuration = serviceDuration,
        customerRequests = customerRequests
    )
}