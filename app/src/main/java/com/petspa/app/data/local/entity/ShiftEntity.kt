package com.petspa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shifts")
data class ShiftEntity(
    @PrimaryKey val id: String,
    val date: String,
    val type: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val technicianId: String
)
