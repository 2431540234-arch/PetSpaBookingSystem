package com.petspa.app.network.dto.response

import java.math.BigDecimal

data class ServiceResponse(
    val id: Long,
    val name: String,
    val category: String = "",
    val price: BigDecimal,
    val duration: Int,
    val emoji: String = "✨",
    val description: String = "",
    val status: String = "active",
    val imageUrl: String = ""
)
