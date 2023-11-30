package com.riviem.sunalarm.core.data.api.sunrise

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.riviem.sunalarm.core.data.api.sunrise.models.SunriseResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface SunriseApiService {
    @GET("json")
    fun getSunriseTime(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double
    ): Call<SunriseResponse>
}

object RetrofitInstance {
    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.sunrisesunset.io/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
    val sunriseApiService: SunriseApiService by lazy {
        retrofit.create(SunriseApiService::class.java)
    }
}


