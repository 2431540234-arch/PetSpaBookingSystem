package com.petspa.app.data.local.dao

import androidx.room.*
import com.petspa.app.data.local.entity.ShiftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftDao {
    @Query("SELECT * FROM shifts")
    fun observeShifts(): Flow<List<ShiftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shifts: List<ShiftEntity>)

    @Query("DELETE FROM shifts")
    suspend fun deleteAll()
}
