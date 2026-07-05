package com.petspa.app.data.local.mapper

import com.petspa.app.data.local.entity.TechnicianProfileEntity
import com.petspa.app.model.TechnicianProfile

fun TechnicianProfileEntity.toDomain(): TechnicianProfile = TechnicianProfile(
    id = id,
    name = name,
    email = email,
    phone = phone,
    position = position,
    avatar = avatar,
    expertise = expertise,
    gender = gender,
    birthDate = birthDate,
    address = address,
    joinDate = joinDate
)

fun TechnicianProfile.toEntity(): TechnicianProfileEntity = TechnicianProfileEntity(
    id = id,
    name = name,
    email = email,
    phone = phone,
    position = position,
    avatar = avatar,
    expertise = expertise,
    gender = gender,
    birthDate = birthDate,
    address = address,
    joinDate = joinDate
)
