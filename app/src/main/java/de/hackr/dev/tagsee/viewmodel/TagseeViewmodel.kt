package de.hackr.dev.tagsee.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hackr.dev.tagsee.data.DataStoreRepository
import de.hackr.dev.tagsee.network.TagseeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class TagseeViewmodel @Inject constructor(
    private val repository: DataStoreRepository
) : ViewModel() {

    val lobbyUserGallerySize: MutableState<Int> = mutableStateOf(0)

    private val TAG = "TagseeViewmodel"

    // this builds a flow of the current taggedphotos instance
    // it should come full circle by saving the serialized meta to express,
    // reloading it from the server and saving it to the preferences datastore
    val taggedPhotosFlow = repository.getImagesState()
        .combine(repository.getMetadataState()) { images, metadata ->
            TaggedPhotos.fromSerialized(images.split("\n"), metadata.split("\n"))
        }

    fun getGallery() = taggedPhotosFlow

    private val currentTags = taggedPhotosFlow.mapLatest { it.getTags() }

    fun getTags() = currentTags

    fun changeUser(username: String) {
        saveUsername(username)
        reloadGallery(username)
        reloadMeta(username)
    }

    fun saveUsername(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveUsernameState(username)
        }
    }

    fun getUsername(): Flow<String> {
        return repository.getUsernameState()
    }

    fun saveMetadata(metadata: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveMetadataState(metadata = metadata)
        }
    }

//    fun getMetadata(): Flow<String> {
//        return repository.getMetadataState()
//    }

    fun saveImages(images: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveImagesState(images)
        }
    }

//    fun getImages(): Flow<String> {
//        return repository.getImagesState()
//    }

    fun reloadGallery(username: String) {
        Log.d(TAG, "reloadGallery()")
        viewModelScope.launch {
            try {
                val listResult = TagseeApi.retrofitService.getGallery(username)
                Log.d(TAG, listResult.joinToString("\n"))
                saveImages(listResult.joinToString("\n"))
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    fun reloadMeta(username: String) {
        Log.d(TAG, "reloadMeta()")
        viewModelScope.launch {
            try {
                val meta = TagseeApi.retrofitService.getMeta2(username)
                Log.d(TAG, meta.toString())
                saveMetadata(meta.joinToString("\n"))
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    fun setMeta(username: String, photos: TaggedPhotos) {
        viewModelScope.launch {
            try {
                val body: RequestBody = photos.serialze()
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                TagseeApi.retrofitService.setMeta(username, body)
                reloadMeta(username)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    fun toggleTag(url: String, tag: String) {
        Log.d(TAG, "going to toggle ${tag} for ${url}")
        viewModelScope.launch {
            try {
                val taggedPhotos = getGallery().first()
                val username = getUsername().first()
                val body: RequestBody = taggedPhotos.serializePendingToggle(url, tag)
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                TagseeApi.retrofitService.setMeta(username, body)
                reloadMeta(username)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    fun getLobbyUserGallerySize(username: String) {
        Log.d(TAG, "getting gallery size for for ${username}")
        lobbyUserGallerySize.value = 0
        viewModelScope.launch {
            try {
                val gallery = TagseeApi.retrofitService.getGallery(username = username)
                lobbyUserGallerySize.value = gallery.size
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }


    // dev
    // resets the metadata for 'markus' to a defined stage
    fun setupUser() {
        setMeta("markus", TaggedPhotos.dummies)
    }
}
