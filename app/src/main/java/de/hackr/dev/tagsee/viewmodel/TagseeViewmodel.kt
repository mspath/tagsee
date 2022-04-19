package de.hackr.dev.tagsee.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hackr.dev.tagsee.data.DataStoreRepository
import de.hackr.dev.tagsee.network.TagseeApi
import de.hackr.dev.tagsee.network.TagseeApiService
import de.hackr.dev.tagsee.util.FilterStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class TagseeViewmodel @Inject constructor(
    private val repository: DataStoreRepository,
    private val apiService: TagseeApiService
) : ViewModel() {

    companion object {
        private val TAG = TagseeViewmodel::class.java.simpleName
    }

    // list of tags a user plans to add
    private var usertags: MutableState<String> = mutableStateOf("cats,dogs")

    // tmp val to validate the existence of a gallery
    val lobbyUserGallerySize: MutableState<Int> = mutableStateOf(0)

    // list of tags to chose from, used for toggling  on or off
    val selectedTagsMap = mutableStateMapOf<String, Boolean>()

    fun toggleSelectedTag(name: String) {
        val tag = selectedTagsMap.get(name)
        tag?.let {
            selectedTagsMap.put(name, !it)
        } ?: selectedTagsMap.put(name, true)
    }

    fun tagSelected(name: String) = selectedTagsMap.getOrDefault(name, false)

    val selectedFilterStrategy: MutableState<FilterStrategy> = mutableStateOf(FilterStrategy.ALL_IMAGES)

    // this builds a flow of the current taggedphotos instance
    // it should come full circle by saving the serialized meta to express,
    // reloading it from the server and saving it to the preferences datastore
    private val taggedPhotosFlow = repository.getImagesState()
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
        usertags.value = ""
        selectedTagsMap.keys.forEach { selectedTagsMap.remove(it) }
    }

    fun resetUser() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveUsernameState("")
        }
    }

    private fun saveUsername(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveUsernameState(username)
        }
    }

    fun getUsername(): Flow<String> {
        return repository.getUsernameState()
    }

    fun getUsertags(): List<String> {
        return usertags.value.split(",")
    }

    fun saveUsertags(newtags: String) {
        usertags.value = newtags
    }

    fun resetUsertags() {
        saveUsertags("cats,cute")
    }

    private fun saveMetadata(metadata: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveMetadataState(metadata = metadata)
        }
    }

    private fun saveImages(images: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveImagesState(images)
        }
    }

    fun reloadGallery(username: String) {
        Log.d(TAG, "reloadGallery()")
        viewModelScope.launch {
            try {
                //val listResult = TagseeApi.retrofitService.getGallery(username)
                val listResult = apiService.getGallery(username)
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
                //val meta = TagseeApi.retrofitService.getMeta(username)
                val meta = apiService.getMeta(username)
                Log.d(TAG, meta.toString())
                saveMetadata(meta.joinToString("\n"))
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    fun updateMeta(username: String, photos: TaggedPhotos) {
        viewModelScope.launch {
            try {
                val body: RequestBody = photos.serialze()
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                //TagseeApi.retrofitService.updateMeta(username, body)
                apiService.updateMeta(username, body)
                reloadMeta(username)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    // toggles a tag of a photo on/off
    fun toggleTag(url: String, tag: String) {
        Log.d(TAG, "going to toggle ${tag} for ${url}")
        viewModelScope.launch {
            try {
                val taggedPhotos = getGallery().first()
                val username = getUsername().first()
                val body: RequestBody = taggedPhotos.serializePendingToggle(url, tag)
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                //TagseeApi.retrofitService.updateMeta(username, body)
                apiService.updateMeta(username, body)
                reloadMeta(username)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    // looks up the gallery size for a user
    // can be used to verify the existence of a user
    fun getLobbyUserGallerySize(username: String) {
        Log.i(TAG, "getting gallery size for ${username}")
        lobbyUserGallerySize.value = 0
        viewModelScope.launch {
            try {
                //val gallery = TagseeApi.retrofitService.getGallery(username = username)
                val gallery = apiService.getGallery(username = username)
                lobbyUserGallerySize.value = gallery.size
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    // dev
    // resets the metadata for the user 'markus' to a mocked stage
    //fun setupUser() {
        //updateMeta("markus", TaggedPhotos.dummies)
        //Log.d(TAG, "setupuser(). currently unused.")
    //}


}
