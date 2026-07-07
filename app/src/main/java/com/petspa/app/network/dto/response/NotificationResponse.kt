package com.petspa.app.network.dto.response

data class NotificationResponse(
    val id: Long,
    val userId: Long,
    val type: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false,
    val relatedId: Long? = null
)
