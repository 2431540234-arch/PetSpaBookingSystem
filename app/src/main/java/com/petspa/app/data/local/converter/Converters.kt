package com.petspa.app.data.local.converter

import androidx.room.TypeConverter
import com.petspa.app.model.UserRole

class Converters {

    @TypeConverter
    fun fromRole(role: UserRole): String {
        return role.name
    }

    @TypeConverter
    fun toRole(value: String): UserRole {
        return UserRole.valueOf(value)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return value.split(",")
    }
}