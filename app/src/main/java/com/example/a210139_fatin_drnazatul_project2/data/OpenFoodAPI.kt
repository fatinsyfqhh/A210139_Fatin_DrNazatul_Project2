package com.example.a210139_fatin_drnazatul_project2.data

import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor

data class OpenFoodResponse(
    val products: List<OpenFoodProduct> = emptyList(),
    val count: Int = 0
)

data class OpenFoodProduct(
    @SerializedName("product_name")
    val product_name: String? = null,
    @SerializedName("categories_tags")
    val categories_tags: List<String>? = null,
    @SerializedName("quantity")
    val quantity: String? = null,
    @SerializedName("brands")
    val brands: String? = null,
    @SerializedName("image_url")
    val image_url: String? = null
)

interface OpenFoodApiService {
    @GET("cgi/search.pl")
    suspend fun searchFood(
        @Query("search_terms") query: String,
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 5,
        @Query("fields") fields: String = "product_name,categories_tags,quantity,brands,image_url"
    ): OpenFoodResponse
}

object OpenFoodApi {
    private const val BASE_URL = "https://world.openfoodfacts.org/"

    private val userAgentInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "ResQBite/1.0 (Android; contact@resqbite.com)")
            .build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(userAgentInterceptor)
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val service: OpenFoodApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodApiService::class.java)
    }
}