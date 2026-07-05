package com.petspa.app.data.local.dao

import androidx.room.*
import com.petspa.app.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications ORDER BY time DESC")
    fun observeNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications ORDER BY time DESC")
    suspend fun getAllNotifications(): List<NotificationEntity>

    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getNotificationById(id: String): NotificationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(
        notifications: List<NotificationEntity>
    )

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()

    @Query("UPDATE notifications SET `read` = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)
}