package com.petspa.app.network.mapper

import com.petspa.app.model.*
import com.petspa.app.network.dto.request.BookingRequest
import com.petspa.app.network.dto.response.*

/**
 * Mappers to convert Network DTOs (Backend with Long IDs) 
 * to Domain Models (Android with String IDs).
 */
object NetworkMapper {
    
    fun PetResponse.toDomain(): Pet = Pet(
        id = id.toString(),
        name = name,
        species = species,
        breed = breed,
        gender = gender,
        age = age,
        weight = weight,
        allergies = allergies,
        medicalHistory = medicalHistory,
        notes = notes,
        emoji = emoji,
        imageUrl = imageUrl,
        customerId = customerId.toString()
    )

    fun ServiceResponse.toDomain(): Service = Service(
        id = id.toString(),
        name = name,
        category = category,
        price = price.toLong(),
        duration = duration,
        emoji = emoji,
        description = description,
        status = status,
        imageUrl = imageUrl
    )

    fun BookingResponse.toDomain(): Booking = Booking(
        id = id.toString(),
        petId = petId.toString(),
        serviceId = serviceId.toString(),
        staffId = staffId?.toString() ?: "",
        customerId = customerId.toString(),
        date = scheduledStart?.substringBefore("T") ?: "",
        time = scheduledStart?.substringAfter("T")?.substring(0, 5) ?: "",
        notes = notes,
        status = status,
        paymentStatus = paymentStatus,
        totalAmount = totalAmount.toLong(),
        paidAmount = paidAmount.toLong(),
        paymentMethod = paymentMethod,
        transactionId = transactionId,
        createdAt = createdAt,
        serviceDuration = serviceDuration,
        customerRequests = customerRequests
    )

    fun BookingDraft.toRequest(): BookingRequest = BookingRequest(
        petId = petId.toLongOrNull() ?: 0L,
        serviceId = serviceId.toLongOrNull() ?: 0L,
        staffId = if (staffId == "any") null else staffId.toLongOrNull(),
        date = date,
        time = time,
        notes = notes
    )

    fun NotificationResponse.toDomain(): NotificationItem = NotificationItem(
        id = id.toString(),
        type = type,
        title = title,
        message = message,
        time = time,
        read = isRead,
        relatedId = relatedId?.toString() ?: ""
    )
}
