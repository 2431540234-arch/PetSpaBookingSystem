package com.petspa.app.network.dto.response

data class PaymentResponseDto(
    val payUrl: String,
    val requestId: String,
    val message: String? = null
)
