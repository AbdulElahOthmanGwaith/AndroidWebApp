package com.gwaith.base.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gwaithbase_settings")

class UserPreferences(private val context: Context) {

    companion object {
        private val HOME_URL = stringPreferencesKey("home_url")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val JAVA_SCRIPT_ENABLED = booleanPreferencesKey("java_script_enabled")
        private val DOM_STORAGE_ENABLED = booleanPreferencesKey("dom_storage_enabled")
        private val FIRST_LAUNCH = booleanPreferencesKey("first_launch")

        const val DEFAULT_HOME_URL = "https://example.com"
    }

    val homeUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[HOME_URL] ?: DEFAULT_HOME_URL
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: true
    }

    val javaScriptEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[JAVA_SCRIPT_ENABLED] ?: true
    }

    val domStorageEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DOM_STORAGE_ENABLED] ?: true
    }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH] ?: true
    }

    suspend fun setHomeUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[HOME_URL] = url
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setJavaScriptEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[JAVA_SCRIPT_ENABLED] = enabled
        }
    }

    suspend fun setDomStorageEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DOM_STORAGE_ENABLED] = enabled
        }
    }

    suspend fun setFirstLaunchCompleted() {
        context.dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = false
        }
    }

    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
