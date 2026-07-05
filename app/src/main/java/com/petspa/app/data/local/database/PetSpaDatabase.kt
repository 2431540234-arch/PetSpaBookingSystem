package com.petspa.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.petspa.app.data.local.converter.Converters
import com.petspa.app.data.local.dao.*
import com.petspa.app.data.local.entity.*

@TypeConverters(Converters::class)
@Database(
    entities = [
        PetEntity::class,
        CustomerEntity::class,
        ServiceEntity::class,
        StaffEntity::class,
        BookingEntity::class,
        UserEntity::class,
        NotificationEntity::class,
        NotifSettingsEntity::class,
        ShiftEntity::class,
        StaffRequestEntity::class,
        RevenueEntity::class,
        TopServiceEntity::class,
        TechnicianProfileEntity::class
    ],
    version = 9,
    exportSchema = false
)
abstract class PetSpaDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao
    abstract fun customerDao(): CustomerDao
    abstract fun serviceDao(): ServiceDao
    abstract fun staffDao(): StaffDao
    abstract fun bookingDao(): BookingDao
    abstract fun userDao(): UserDao
    abstract fun notificationDao(): NotificationDao
    abstract fun notifSettingsDao(): NotifSettingsDao
    abstract fun shiftDao(): ShiftDao
    abstract fun staffRequestDao(): StaffRequestDao
    abstract fun revenueDao(): RevenueDao
    abstract fun technicianProfileDao(): TechnicianProfileDao
}
