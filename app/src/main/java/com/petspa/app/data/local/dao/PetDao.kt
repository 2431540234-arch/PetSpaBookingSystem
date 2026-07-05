package com.petspa.app.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.petspa.app.data.local.entity.PetEntity

@Dao
interface PetDao {

    @Query("SELECT * FROM pets")
    suspend fun getAllPets(): List<PetEntity>

    @Query("SELECT * FROM pets")
    fun observePets(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE id = :id")
    suspend fun getPetById(id: String): PetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: PetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pets: List<PetEntity>)

    @Update
    suspend fun updatePet(pet: PetEntity)

    @Delete
    suspend fun deletePet(pet: PetEntity)

    @Query("DELETE FROM pets")
    suspend fun deleteAll()
}