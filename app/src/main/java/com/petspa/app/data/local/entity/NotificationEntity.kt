package com.petspa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(

    @PrimaryKey
    val id: String,

    val type: String,

    val title: String,

    val message: String,

    val time: String,

    val read: Boolean = false,

    val relatedId: String = ""
)