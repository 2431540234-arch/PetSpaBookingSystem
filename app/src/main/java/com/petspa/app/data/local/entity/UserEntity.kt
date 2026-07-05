package com.petspa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey
    val id: String,

    val name: String,

    val email: String,

    val phone: String,

    val gender: String = "",

    val dob: String = "",

    val address: String = "",

    val role: String = "CUSTOMER"
)