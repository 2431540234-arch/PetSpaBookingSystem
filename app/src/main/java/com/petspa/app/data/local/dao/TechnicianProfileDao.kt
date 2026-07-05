package com.petspa.app.data.local.dao

import androidx.room.*
import com.petspa.app.data.local.entity.TechnicianProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TechnicianProfileDao {
    @Query("SELECT * FROM technician_profile LIMIT 1")
    fun observeProfile(): Flow<TechnicianProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: TechnicianProfileEntity)

    @Query("DELETE FROM technician_profile")
    suspend fun deleteProfile()
}
