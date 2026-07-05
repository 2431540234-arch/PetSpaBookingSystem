package com.petspa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(

    @PrimaryKey
    val id: String,

    val name: String,

    val category: String = "",

    val price: Long,

    val duration: Int,

    val emoji: String = "✨",

    val description: String = "",

    val status: String = "active",

    val imageUrl: String = ""
)