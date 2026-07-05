package com.petspa.app.data.local.dao

import androidx.room.*
import com.petspa.app.data.local.entity.StaffEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StaffDao {

    @Query("SELECT * FROM staff")
    fun observeStaff(): Flow<List<StaffEntity>>

    @Query("SELECT * FROM staff")
    suspend fun getAllStaff(): List<StaffEntity>

    @Query("SELECT * FROM staff WHERE id = :id")
    suspend fun getStaffById(id: String): StaffEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(
        staff: List<StaffEntity>
    )

    @Update
    suspend fun updateStaff(staff: StaffEntity)

    @Delete
    suspend fun deleteStaff(staff: StaffEntity)

    @Query("DELETE FROM staff")
    suspend fun deleteAll()
}