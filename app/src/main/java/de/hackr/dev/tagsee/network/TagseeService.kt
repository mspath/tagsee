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

const val BASE_URL =
    "http://192.168.178.41:3000"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface TagseeApiService {
    @GET("{galleryname}/images")
    suspend fun getGalleryImages(@Path("galleryname", encoded = true) galleryname: String): List<String>

    @GET("{galleryname}/meta")
    suspend fun getGalleryMeta(@Path("galleryname", encoded = true) galleryname: String): List<String>

    @GET("gallerynames")
    suspend fun getGallerynames(): List<String>

    @Headers("Content-Type: text/plain")
    @POST("{galleryname}/meta")
    suspend fun postGalleryMeta(@Path("galleryname", encoded = true) galleryname: String, @Body meta: RequestBody)
}

object TagseeApi {
    val retrofitService : TagseeApiService by lazy {
        retrofit.create(TagseeApiService::class.java)
    }

    val mockService : MockApiService by lazy {
        MockApiService
    }
}