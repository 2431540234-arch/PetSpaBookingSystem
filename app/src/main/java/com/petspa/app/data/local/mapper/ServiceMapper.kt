package com.petspa.app.data.local.mapper

import com.petspa.app.data.local.entity.ServiceEntity
import com.petspa.app.model.Service

fun ServiceEntity.toService(): Service {
    return Service(
        id = id,
        name = name,
        category = category,
        price = price,
        duration = duration,
        emoji = emoji,
        description = description,
        status = status,
        imageUrl = imageUrl
    )
}

fun Service.toEntity(): ServiceEntity {
    return ServiceEntity(
        id = id,
        name = name,
        category = category,
        price = price,
        duration = duration,
        emoji = emoji,
        description = description,
        status = status,
        imageUrl = imageUrl
    )
}