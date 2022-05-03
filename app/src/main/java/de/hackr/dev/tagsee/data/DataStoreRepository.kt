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

    private val dataStore = context.dataStore

    private object PreferencesKey {
        val gallerynameKey = stringPreferencesKey(name = "galleryname")
        val metadataKey = stringPreferencesKey(name = "metadata")
        val imagesKey = stringPreferencesKey(name = "images")
    }

    suspend fun saveGallerynameState(galleryname: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.gallerynameKey] = galleryname
        }
    }

    fun getGallerynameState(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException)
                    emit(emptyPreferences())
                else
                    throw exception
            }
            .map { preferences ->
                val galleryname = preferences[PreferencesKey.gallerynameKey] ?: ""
                galleryname
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
