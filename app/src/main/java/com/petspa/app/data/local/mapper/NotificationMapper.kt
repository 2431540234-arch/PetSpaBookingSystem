package com.petspa.app.data.local.mapper

import com.petspa.app.data.local.entity.NotificationEntity
import com.petspa.app.model.NotificationItem

fun NotificationEntity.toNotification(): NotificationItem {
    return NotificationItem(
        id = id,
        type = type,
        title = title,
        message = message,
        time = time,
        read = read,
        relatedId = relatedId
    )
}

fun NotificationItem.toEntity(): NotificationEntity {
    return NotificationEntity(
        id = id,
        type = type,
        title = title,
        message = message,
        time = time,
        read = read,
        relatedId = relatedId
    )
}