package com.petspa.app.network.dto.response

import java.math.BigDecimal

data class BookingResponse(
    val id: Long,
    val petId: Long,
    val serviceId: Long,
    val staffId: Long? = null,
    val customerId: Long,
    val scheduledStart: String? = null,
    val scheduledEnd: String? = null,
    val notes: String = "",
    val status: String = "pending",
    val paymentStatus: String = "unpaid",
    val totalAmount: BigDecimal = BigDecimal.ZERO,
    val paidAmount: BigDecimal = BigDecimal.ZERO,
    val paymentMethod: String = "",
    val transactionId: String = "",
    val createdAt: String = "",
    val serviceDuration: Int = 60,
    val customerRequests: String = ""
)
