package com.petspa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(

    @PrimaryKey
    val id: String,

    val petId: String = "",
    val petName: String = "",

    val serviceId: String = "",
    val serviceName: String = "",

    val staffId: String = "",
    val staffName: String = "",

    val customerId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",

    val date: String,

    val time: String,

    val notes: String = "",

    val status: String = "pending",

    val paymentStatus: String = "unpaid",

    val totalAmount: Long = 0,

    val paidAmount: Long = 0,

    val paymentMethod: String = "",

    val transactionId: String = "",

    val createdAt: String = "",

    val serviceDuration: Int = 60,

    val customerRequests: String = ""
)