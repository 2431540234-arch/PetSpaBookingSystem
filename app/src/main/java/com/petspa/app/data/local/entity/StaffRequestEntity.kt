package com.petspa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "staff_requests")
data class StaffRequestEntity(
    @PrimaryKey val id: String,
    val staffId: String,
    val staffName: String,
    val type: String,
    val date: String,
    val reason: String,
    val status: String,
    val createdAt: String
)
