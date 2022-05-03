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

object MockApiService : TagseeApiService {

    // mock db which stores the metadata as string
    var taggedPhotos = ""

    override suspend fun getGalleryImages(galleryname: String): List<String> {
        val baseurl = "https://hackr.de/tagsee/public/"

        val imagesEric = listOf(
            "flat1.jpg",
            "flat2.jpg",
            "flat3.jpg",
            "flat4.jpg").map { "${baseurl}eric/images/${it}" }
        val imagesDippemess = listOf(
            "IMG_1632.jpg",
            "IMG_1635.jpg",
            "IMG_1638.jpg",
            "IMG_1641.jpg",
            "IMG_1682.jpg",
            "IMG_1689.jpg",
            "IMG_1690.jpg",
            "IMG_1695.jpg",
            "IMG_1703.jpg").map { "${baseurl}dippemess/images/${it}" }
        val imagesSchirn = listOf(
            "IMG_2254.jpg",
            "IMG_2260.jpg",
            "IMG_2265.jpg",
            "IMG_2266.jpg",
            "IMG_2268.jpg",
            "IMG_2274.jpg",
            "IMG_2277.jpg",
            "IMG_2284.jpg",
            "IMG_2296.jpg",
            "IMG_2297.jpg").map { "${baseurl}schirn/images/${it}" }
        return if (galleryname == "dippemess") imagesDippemess
            else if (galleryname == "schirn") imagesSchirn
            else imagesEric
    }

    override suspend fun getGalleryMeta(galleryname: String) = taggedPhotos.split(" ")

    override suspend fun getGallerynames() = listOf(
        "eric",
        "dippemess",
        "schirn")

    // NOTE this is a cumbersome workaround but needs to be refactored in the viewmodel
    //  but for now I want to keep the API simple
    override suspend fun postGalleryMeta(galleryname: String, meta: RequestBody) {
        val buffer = okio.Buffer()
        meta.writeTo(buffer)
        val body = buffer.readString(buffer.size, Charset.forName("UTF-8"))
        Log.d("MOCK", body)
        taggedPhotos  = body
    }
}