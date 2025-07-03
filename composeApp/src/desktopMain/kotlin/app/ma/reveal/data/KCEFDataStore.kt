package app.ma.reveal.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class KCEFDataStore(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val IS_INITIALIZED = booleanPreferencesKey("is_kcef_initialized")
    }

    val isInitialized: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_INITIALIZED] ?: false
    }

    suspend fun setInitialized(initialized: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_INITIALIZED] = initialized
        }
    }

    suspend fun checkAndSetInitialized(): Boolean {
        val currentState = isInitialized.first()
        if (!currentState) {
            setInitialized(true)
            return false
        }
        return true
    }
}