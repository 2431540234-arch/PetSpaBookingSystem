package com.petspa.app.data.local.mapper

import com.petspa.app.data.local.entity.StaffEntity
import com.petspa.app.model.StaffMember

fun StaffEntity.toStaff(): StaffMember {
    return StaffMember(
        id = id,
        name = name,
        email = email,
        phone = phone,
        specialty = specialty,
        role = role,
        emoji = emoji,
        status = status,
        completedBookings = completedBookings,
        customers = customers,
        createdAt = createdAt
    )
}

fun StaffMember.toEntity(): StaffEntity {
    return StaffEntity(
        id = id,
        name = name,
        email = email,
        phone = phone,
        specialty = specialty,
        role = role,
        emoji = emoji,
        status = status,
        completedBookings = completedBookings,
        customers = customers,
        createdAt = createdAt
    )
}