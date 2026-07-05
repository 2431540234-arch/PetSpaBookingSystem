package com.petspa.app.data.local.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: PetSpaDatabase? = null

    fun getDatabase(
        context: Context
    ): PetSpaDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance =
                Room.databaseBuilder(
                    context.applicationContext,
                    PetSpaDatabase::class.java,
                    "petspa_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

            INSTANCE = instance
            instance
        }
    }
}