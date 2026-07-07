package com.petspa.app.network.dto.response

data class PetResponse(
    val id: Long,
    val name: String,
    val species: String,
    val breed: String,
    val gender: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val allergies: String = "",
    val medicalHistory: String = "",
    val notes: String = "",
    val emoji: String = "🐾",
    val imageUrl: String? = null,
    val customerId: Long
)
