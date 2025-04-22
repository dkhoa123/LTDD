// UserPreferences.kt
package com.example.uiappchat.Model

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        private val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    val rememberMeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[REMEMBER_ME_KEY] ?: false }

    val userIdFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[USER_ID_KEY] }

    suspend fun saveRememberMe(remember: Boolean) {
        context.dataStore.edit { prefs -> prefs[REMEMBER_ME_KEY] = remember }
    }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { prefs -> prefs[USER_ID_KEY] = userId }
    }

    suspend fun clearUserData() {
        context.dataStore.edit { prefs ->
            prefs.remove(REMEMBER_ME_KEY)
            prefs.remove(USER_ID_KEY)
        }
    }
}
