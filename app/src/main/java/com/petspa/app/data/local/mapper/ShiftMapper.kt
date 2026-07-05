package com.petspa.app.data.local.mapper

import com.petspa.app.data.local.entity.ShiftEntity
import com.petspa.app.model.Shift

fun ShiftEntity.toShift(): Shift = Shift(
    id = id,
    date = date,
    type = type,
    startTime = startTime,
    endTime = endTime,
    status = status,
    technicianId = technicianId
)

fun Shift.toEntity(): ShiftEntity = ShiftEntity(
    id = id,
    date = date,
    type = type,
    startTime = startTime,
    endTime = endTime,
    status = status,
    technicianId = technicianId
)
