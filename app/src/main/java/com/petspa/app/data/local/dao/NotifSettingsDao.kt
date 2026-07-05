package com.petspa.app.data.local.dao

import androidx.room.*
import com.petspa.app.data.local.entity.NotifSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotifSettingsDao {
    @Query("SELECT * FROM notif_settings LIMIT 1")
    fun observeSettings(): Flow<NotifSettingsEntity?>

    @Query("SELECT * FROM notif_settings LIMIT 1")
    suspend fun getSettings(): NotifSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: NotifSettingsEntity)

    @Update
    suspend fun updateSettings(settings: NotifSettingsEntity)

    @Query("DELETE FROM notif_settings")
    suspend fun deleteAll()
}