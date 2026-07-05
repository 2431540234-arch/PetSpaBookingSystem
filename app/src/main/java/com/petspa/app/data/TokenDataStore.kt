package com.petspa.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.petspa.app.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Lưu token và role sau đăng nhập
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "petspa_auth")

class TokenDataStore(private val context: Context) {
    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_ROLE = stringPreferencesKey("user_role")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    }

    val authFlow: Flow<AuthSession?> = context.dataStore.data.map { prefs ->
        val token = prefs[KEY_TOKEN] ?: return@map null
        val roleStr = prefs[KEY_ROLE] ?: return@map null
        AuthSession(
            token = token,
            role = UserRole.valueOf(roleStr),
            userId = prefs[KEY_USER_ID] ?: "",
            userName = prefs[KEY_USER_NAME] ?: "",
            userEmail = prefs[KEY_USER_EMAIL] ?: ""
        )
    }

    suspend fun saveSession(token: String, role: UserRole, userId: String, userName: String, userEmail: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_ROLE] = role.name
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USER_NAME] = userName
            prefs[KEY_USER_EMAIL] = userEmail
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}

data class AuthSession(
    val token: String,
    val role: UserRole,
    val userId: String,
    val userName: String,
    val userEmail: String
)
