package de.hackr.dev.tagsee.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class DataStoreRepository(context: Context) {

    // should be refactored to use Room
    private val dataStore = context.dataStore

    private object PreferencesKey {
        val usernameKey = stringPreferencesKey(name = "username")
        val metadataKey = stringPreferencesKey(name = "metadata")
        val imagesKey = stringPreferencesKey(name = "images")
    }

    suspend fun saveUsernameState(username: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.usernameKey] = username
        }
    }

    fun getUsernameState(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException)
                    emit(emptyPreferences())
                else
                    throw exception
            }
            .map { preferences ->
                val username = preferences[PreferencesKey.usernameKey] ?: ""
                username
            }
    }

    suspend fun saveMetadataState(metadata: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.metadataKey] = metadata
        }
    }

    fun getMetadataState(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException)
                    emit(emptyPreferences())
                else
                    throw exception
            }
            .map { preferences ->
                val metadata = preferences[PreferencesKey.metadataKey] ?: ""
                metadata
            }
    }

    suspend fun saveImagesState(images: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.imagesKey] = images
        }
    }

    fun getImagesState(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException)
                    emit(emptyPreferences())
                else
                    throw exception
            }
            .map { preferences ->
                val images = preferences[PreferencesKey.imagesKey] ?: ""
                images
            }
    }
}
