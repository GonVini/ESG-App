package com.esgapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object AirQualityRepository {
    private val service: OpenMeteoService by lazy {
        Retrofit.Builder()
            .baseUrl("https://air-quality-api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoService::class.java)
    }

    suspend fun fetchPm25(latitude: Double, longitude: Double): Result<Double> {
        return runCatching {
            val response = service.getCurrentAirQuality(latitude = latitude, longitude = longitude)
            response.current.pm25
        }
    }
}

interface OpenMeteoService {
    @GET("v1/air-quality")
    suspend fun getCurrentAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "pm2_5"
    ): OpenMeteoResponse
}

data class OpenMeteoResponse(
    val current: CurrentAirData
)

data class CurrentAirData(
    @com.google.gson.annotations.SerializedName("pm2_5")
    val pm25: Double
)
