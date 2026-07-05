package com.petspa.app.data.local.mapper

import com.petspa.app.data.local.entity.RevenueEntity
import com.petspa.app.data.local.entity.TopServiceEntity
import com.petspa.app.model.RevenuePoint
import com.petspa.app.model.TopService

fun RevenueEntity.toRevenuePoint(): RevenuePoint = RevenuePoint(
    label = label,
    revenue = revenue,
    bookings = bookings
)

fun RevenuePoint.toEntity(type: String): RevenueEntity = RevenueEntity(
    type = type,
    label = label,
    revenue = revenue,
    bookings = bookings
)

fun TopServiceEntity.toTopService(): TopService = TopService(
    name = name,
    count = count,
    revenue = revenue
)

fun TopService.toEntity(): TopServiceEntity = TopServiceEntity(
    name = name,
    count = count,
    revenue = revenue
)
