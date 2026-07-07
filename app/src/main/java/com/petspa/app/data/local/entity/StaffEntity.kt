package com.petspa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "staff")
data class StaffEntity(

    @PrimaryKey
    val id: String,

    val name: String,

    val email: String = "",

    val phone: String = "",

    val specialty: String = "",

    val role: String = "",

    val emoji: String = "👤",

    val avatar: String? = null,

    val status: String = "active",

    val completedBookings: Int = 0,

    val customers: Int = 0,

    val createdAt: String = ""
)
