package com.petspa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notif_settings")
data class NotifSettingsEntity(

    @PrimaryKey
    val id: Int = 1,

    val bookingNew: Boolean = true,
    val bookingConfirmed: Boolean = true,
    val bookingRescheduled: Boolean = true,
    val bookingCancelled: Boolean = true,

    val paymentSuccess: Boolean = true,
    val paymentFailed: Boolean = true,

    val serviceInProgress: Boolean = true,
    val serviceCompleted: Boolean = true,

    val channelPush: Boolean = true,
    val channelEmail: Boolean = false
)