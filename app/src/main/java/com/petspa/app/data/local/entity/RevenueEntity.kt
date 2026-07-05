package com.petspa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "revenue_points")
data class RevenueEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "DAILY" or "MONTHLY"
    val label: String,
    val revenue: Long,
    val bookings: Int
)

@Entity(tableName = "top_services")
data class TopServiceEntity(
    @PrimaryKey val name: String,
    val count: Int,
    val revenue: Long
)
