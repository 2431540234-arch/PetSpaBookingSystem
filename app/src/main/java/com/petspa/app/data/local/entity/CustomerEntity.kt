package com.petspa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(

    @PrimaryKey
    val id: String,

    val name: String,
    val email: String,
    val phone: String,

    val status: String = "active",
    val createdAt: String = "",

    val gender: String = "",
    val dob: String = "",
    val address: String = "",

    val totalBookings: Int = 0,
    val completedBookings: Int = 0,
    val cancelledBookings: Int = 0,

    val totalSpent: Long = 0
)