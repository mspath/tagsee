package de.hackr.dev.tagsee.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hackr.dev.tagsee.data.DataStoreRepository
import de.hackr.dev.tagsee.model.Cover
import de.hackr.dev.tagsee.model.TaggedPhotos
import de.hackr.dev.tagsee.network.TagseeApiService
import de.hackr.dev.tagsee.components.FilterStrategy
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
    private var usertags: MutableState<String> = mutableStateOf("something,nothing")

    // list of the covers of the galleries
    val covers: MutableState<List<Cover>> = mutableStateOf(emptyList())

    // size of the gallery selected in the lobby.
    val lobbyGallerySize: MutableState<Int> = mutableStateOf(0)

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

    // this builds a flow of the current gallery
    // it should come full circle by saving the serialized meta to the web-service,
    // reloading it from the server and saving it to the preferences datastore
    private val taggedPhotosFlow = repository.getImagesState()
        .combine(repository.getMetadataState()) { images, metadata ->
            TaggedPhotos.fromSerialized(images.split("\n"), metadata.split("\n"))
        }

    fun getGallery() = taggedPhotosFlow

    private val currentTags = taggedPhotosFlow.mapLatest { it.getTags() }

    fun getTags() = currentTags

    fun changeGallery(galleryname: String) {
        saveGalleryname(galleryname)
        reloadGalleryImages(galleryname)
        reloadGalleryMeta(galleryname)
        usertags.value = ""
        selectedTagsMap.keys.forEach { selectedTagsMap.remove(it) }
    }

    fun resetGallery() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveGallerynameState("")
        }
    }

    private fun saveGalleryname(galleryname: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveGallerynameState(galleryname)
        }
    }

    fun getGalleryname(): Flow<String> {
        return repository.getGallerynameState()
    }

    fun getUsertags(): List<String> {
        return usertags.value.split(",")
    }

    fun saveUsertags(newtags: String) {
        usertags.value = newtags
    }

    fun resetUsertags() {
        saveUsertags("star")
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

    fun reloadGalleryImages(galleryname: String) {
        viewModelScope.launch {
            try {
                val listResult = apiService.getGalleryImages(galleryname)
                saveImages(listResult.joinToString("\n"))
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    fun reloadGalleryMeta(galleryname: String) {
        viewModelScope.launch {
            try {
                val meta = apiService.getGalleryMeta(galleryname)
                saveMetadata(meta.joinToString("\n"))
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    fun updateGalleryMeta(galleryname: String, photos: TaggedPhotos) {
        viewModelScope.launch {
            try {
                val body: RequestBody = photos.serialze()
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                apiService.postGalleryMeta(galleryname, body)
                reloadGalleryMeta(galleryname)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    fun getCovers() {
        viewModelScope.launch {
            try {
                val gallerynames = apiService.getGallerynames()
                covers.value = gallerynames.map { galleryname ->
                    val images = apiService.getGalleryImages(galleryname)
                    val imageLeft = if (images.size > 0) images[0] else ""
                    val imageRight = if (images.size > 1) images[1] else ""
                    Cover(galleryname, imageLeft, imageRight)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    // toggles a tag of a photo on/off
    // NOTE this will trigger a full circle refresh of the state
    fun toggleTag(url: String, tag: String) {
        viewModelScope.launch {
            try {
                val taggedPhotos = getGallery().first()
                val galleryname = getGalleryname().first()
                val body: RequestBody = taggedPhotos.serializePendingToggle(url, tag)
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                apiService.postGalleryMeta(galleryname, body)
                reloadGalleryMeta(galleryname)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    fun getLobbyGallerySize(galleryname: String) {
        Log.i(TAG, "getting gallery size for ${galleryname}")
        lobbyGallerySize.value = 0
        viewModelScope.launch {
            try {
                val gallery = apiService.getGalleryImages(galleryname)
                lobbyGallerySize.value = gallery.size
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
}
