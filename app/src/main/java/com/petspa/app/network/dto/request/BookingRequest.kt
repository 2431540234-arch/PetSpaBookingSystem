package com.petspa.app.network.dto.request

data class BookingRequest(
    val petId: Long,
    val serviceId: Long,
    val staffId: Long? = null,
    val date: String,
    val time: String,
    val notes: String? = null,
    val customerRequests: String? = null,
    val serviceDuration: Int? = 60
)
