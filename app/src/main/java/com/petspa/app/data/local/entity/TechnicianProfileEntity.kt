package com.petspa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "technician_profile")
data class TechnicianProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val position: String,
    val avatar: String,
    val expertise: List<String>,
    val gender: String,
    val birthDate: String,
    val address: String,
    val joinDate: String
)
