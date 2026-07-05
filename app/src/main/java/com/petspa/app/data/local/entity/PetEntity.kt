package com.petspa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(

    @PrimaryKey
    val id: String,

    val name: String,
    val species: String,
    val breed: String,
    val gender: String,
    val age: Int,
    val weight: Double,
    val allergies: String,
    val medicalHistory: String,
    val notes: String,
    val emoji: String,
    val imageUrl: String?,
    val customerId: String
)
