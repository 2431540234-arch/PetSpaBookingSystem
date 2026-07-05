package com.petspa.app.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.petspa.app.data.local.entity.ServiceEntity

@Dao
interface ServiceDao {

    @Query("SELECT * FROM services")
    suspend fun getAllServices(): List<ServiceEntity>

    @Query("SELECT * FROM services")
    fun observeServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getServiceById(id: String): ServiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<ServiceEntity>)

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Delete
    suspend fun deleteService(service: ServiceEntity)

    @Query("DELETE FROM services")
    suspend fun deleteAll()
}