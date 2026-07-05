package com.petspa.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.petspa.app.model.BookingDraft
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.bookingDataStore: DataStore<Preferences> by preferencesDataStore(name = "petspa_booking")

class BookingDataStore(private val context: Context) {
    companion object {
        private val KEY_DRAFT = stringPreferencesKey("booking_draft")
        private val KEY_LAST_ROUTE = stringPreferencesKey("last_route")
    }

    private val gson = Gson()

    val draftFlow: Flow<BookingDraft> = context.bookingDataStore.data.map { prefs ->
        val json = prefs[KEY_DRAFT]
        if (json != null) {
            try {
                gson.fromJson(json, BookingDraft::class.java)
            } catch (e: Exception) {
                BookingDraft()
            }
        } else {
            BookingDraft()
        }
    }

    val lastRouteFlow: Flow<String?> = context.bookingDataStore.data.map { prefs ->
        prefs[KEY_LAST_ROUTE]
    }

    suspend fun saveDraft(draft: BookingDraft) {
        context.bookingDataStore.edit { prefs ->
            prefs[KEY_DRAFT] = gson.toJson(draft)
        }
    }

    suspend fun saveLastRoute(route: String?) {
        context.bookingDataStore.edit { prefs ->
            if (route == null) {
                prefs.remove(KEY_LAST_ROUTE)
            } else {
                prefs[KEY_LAST_ROUTE] = route
            }
        }
    }

    suspend fun clear() {
        context.bookingDataStore.edit { it.clear() }
    }
}
