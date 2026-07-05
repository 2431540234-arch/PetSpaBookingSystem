package com.petspa.app.data.local.dao

import androidx.room.*
import com.petspa.app.data.local.entity.RevenueEntity
import com.petspa.app.data.local.entity.TopServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RevenueDao {
    @Query("SELECT * FROM revenue_points WHERE type = :type")
    fun observeRevenue(type: String): Flow<List<RevenueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevenue(points: List<RevenueEntity>)

    @Query("DELETE FROM revenue_points WHERE type = :type")
    suspend fun deleteRevenueByType(type: String)

    @Query("SELECT * FROM top_services ORDER BY count DESC")
    fun observeTopServices(): Flow<List<TopServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopServices(services: List<TopServiceEntity>)

    @Query("DELETE FROM top_services")
    suspend fun deleteAllTopServices()
}
