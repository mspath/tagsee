package de.hackr.dev.tagsee.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

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

    // FIXME rename here and on express
    @GET("{username}/meta2.json")
    suspend fun getMeta2(@Path("username", encoded = true) username: String): List<String>

    // FIXME rename here and on express
    @Headers("Content-Type: text/plain")
    @POST("{username}/replacemeta")
    suspend fun setMeta(@Path("username", encoded = true) username: String, @Body meta: RequestBody)
}

object TagseeApi {
    val retrofitService : TagseeApiService by lazy {
        retrofit.create(TagseeApiService::class.java)
    }
}