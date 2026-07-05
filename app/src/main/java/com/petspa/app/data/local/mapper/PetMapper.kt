package com.petspa.app.data.local.mapper

import com.petspa.app.data.local.entity.PetEntity
import com.petspa.app.model.Pet

fun PetEntity.toPet(): Pet {
    return Pet(
        id = id,
        name = name,
        species = species,
        breed = breed,
        gender = gender,
        age = age,
        weight = weight,
        allergies = allergies,
        medicalHistory = medicalHistory,
        notes = notes,
        emoji = emoji,
        imageUrl = imageUrl,
        customerId = customerId
    )
}

fun Pet.toEntity(): PetEntity {
    return PetEntity(
        id = id,
        name = name,
        species = species,
        breed = breed,
        gender = gender,
        age = age,
        weight = weight,
        allergies = allergies,
        medicalHistory = medicalHistory,
        notes = notes,
        emoji = emoji,
        imageUrl = imageUrl,
        customerId = customerId
    )
}