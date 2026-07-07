package com.petspa.app.network.dto.request

import java.math.BigDecimal

data class PaymentRequest(
    val paymentStatus: String,
    val paidAmount: BigDecimal? = null,
    val transactionId: String? = null
)
