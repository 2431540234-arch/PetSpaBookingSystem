package com.petspa.app.network.dto.request

data class PaymentCreateRequest(
    val bookingId: Long,
    val gateway: String // MOMO, VNPAY, ZALOPAY
)
