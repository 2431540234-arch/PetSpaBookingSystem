package com.petspa.app.data.local.mapper

import com.petspa.app.data.local.entity.StaffRequestEntity
import com.petspa.app.model.StaffRequest

fun StaffRequestEntity.toStaffRequest(): StaffRequest = StaffRequest(
    id = id,
    staffId = staffId,
    staffName = staffName,
    type = type,
    date = date,
    reason = reason,
    status = status,
    createdAt = createdAt
)

fun StaffRequest.toEntity(): StaffRequestEntity = StaffRequestEntity(
    id = id,
    staffId = staffId,
    staffName = staffName,
    type = type,
    date = date,
    reason = reason,
    status = status,
    createdAt = createdAt
)
