package com.petspa.app.data.local.mapper

import com.petspa.app.data.local.entity.CustomerEntity
import com.petspa.app.model.Customer

fun CustomerEntity.toCustomer(): Customer {
    return Customer(
        id = id,
        name = name,
        email = email,
        phone = phone,
        status = status,
        createdAt = createdAt,
        gender = gender,
        dob = dob,
        address = address,
        totalBookings = totalBookings,
        completedBookings = completedBookings,
        cancelledBookings = cancelledBookings,
        totalSpent = totalSpent
    )
}

fun Customer.toEntity(): CustomerEntity {
    return CustomerEntity(
        id = id,
        name = name,
        email = email,
        phone = phone,
        status = status,
        createdAt = createdAt,
        gender = gender,
        dob = dob,
        address = address,
        totalBookings = totalBookings,
        completedBookings = completedBookings,
        cancelledBookings = cancelledBookings,
        totalSpent = totalSpent
    )
}