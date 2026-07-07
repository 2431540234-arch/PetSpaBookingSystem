package com.petspa.app.network.dto.response

import java.math.BigDecimal

data class PaymentTransactionDto(
    val id: Long,
    val bookingId: Long,
    val paymentGateway: String,
    val transactionId: String? = null,
    val requestId: String,
    val amount: BigDecimal,
    val currency: String = "VND",
    val paymentStatus: String,
    val payUrl: String? = null,
    val createdAt: String? = null,
    val paidAt: String? = null
)
