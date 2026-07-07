package com.petspa.app.network.dto.request

data class UpdateUserRequest(
    val name: String? = null,
    val phone: String? = null,
    val gender: String? = null,
    val dob: String? = null,
    val address: String? = null,
    val avatarUrl: String? = null
)
