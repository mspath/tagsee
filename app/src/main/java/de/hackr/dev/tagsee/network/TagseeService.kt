package de.hackr.dev.tagsee.network

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.nio.Buffer
import java.nio.charset.Charset

private const val BASE_URL =
    "http://192.168.178.41:3000"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface TagseeApiService {
    @GET("{username}/gallery.json")
    suspend fun getGallery(@Path("username", encoded = true) username: String): List<String>

    @GET("{username}/meta.json")
    suspend fun getMeta(@Path("username", encoded = true) username: String): List<String>

    @Headers("Content-Type: text/plain")
    @POST("{username}/update-meta")
    suspend fun updateMeta(@Path("username", encoded = true) username: String, @Body meta: RequestBody)
}

object TagseeApi {
    val retrofitService : TagseeApiService by lazy {
        retrofit.create(TagseeApiService::class.java)
    }

    val mockService : MockApiService by lazy {
        MockApiService
    }
}

object MockApiService : TagseeApiService {

    var taggedPhotos = "http://hackr.de/img/hackr_logo.jpg<logo,tree>"

    override suspend fun getGallery(username: String): List<String> {
        val photos = listOf("http://hackr.de/img/hackr_logo.jpg",
            "http://hackr.de/img/worldwide/hackr.png")
        return photos
    }

    override suspend fun getMeta(username: String): List<String> {
        return taggedPhotos.split(" ")
    }

    override suspend fun updateMeta(username: String, meta: RequestBody) {
        val buffer = okio.Buffer()
        meta.writeTo(buffer)
        taggedPhotos  = buffer.clone().readString(buffer.size, Charset.forName("UTF-8"))
    }
}