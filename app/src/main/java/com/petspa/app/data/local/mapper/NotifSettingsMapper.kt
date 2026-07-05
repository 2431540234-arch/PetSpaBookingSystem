package com.petspa.app.data.local.mapper

import com.petspa.app.data.local.entity.NotifSettingsEntity
import com.petspa.app.model.NotifSettings

fun NotifSettingsEntity.toNotifSettings(): NotifSettings {
    return NotifSettings(
        bookingNew = bookingNew,
        bookingConfirmed = bookingConfirmed,
        bookingRescheduled = bookingRescheduled,
        bookingCancelled = bookingCancelled,
        paymentSuccess = paymentSuccess,
        paymentFailed = paymentFailed,
        serviceInProgress = serviceInProgress,
        serviceCompleted = serviceCompleted,
        channelPush = channelPush,
        channelEmail = channelEmail
    )
}

fun NotifSettings.toEntity(): NotifSettingsEntity {
    return NotifSettingsEntity(
        bookingNew = bookingNew,
        bookingConfirmed = bookingConfirmed,
        bookingRescheduled = bookingRescheduled,
        bookingCancelled = bookingCancelled,
        paymentSuccess = paymentSuccess,
        paymentFailed = paymentFailed,
        serviceInProgress = serviceInProgress,
        serviceCompleted = serviceCompleted,
        channelPush = channelPush,
        channelEmail = channelEmail
    )
}