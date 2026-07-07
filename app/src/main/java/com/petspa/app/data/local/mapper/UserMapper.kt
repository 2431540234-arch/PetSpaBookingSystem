package com.petspa.app.data.local.mapper

import com.petspa.app.data.local.entity.UserEntity
import com.petspa.app.model.User
import com.petspa.app.model.UserRole

fun UserEntity.toUser(): User {
    val userRole = try {
        UserRole.valueOf(role)
    } catch (e: Exception) {
        UserRole.CUSTOMER
    }
    return User(
        id = id,
        name = name,
        email = email,
        phone = phone,
        gender = gender,
        dob = dob,
        address = address,
        role = userRole,
        avatarUrl = avatarUrl
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        phone = phone,
        gender = gender,
        dob = dob,
        address = address,
        role = role.name,
        avatarUrl = avatarUrl
    )
}
