package com.petspa.app.data.local.dao

import androidx.room.*
import com.petspa.app.data.local.entity.StaffRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StaffRequestDao {
    @Query("SELECT * FROM staff_requests ORDER BY createdAt DESC")
    fun observeStaffRequests(): Flow<List<StaffRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requests: List<StaffRequestEntity>)

    @Query("DELETE FROM staff_requests")
    suspend fun deleteAll()
}
