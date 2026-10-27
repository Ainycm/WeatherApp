package com.mdi2.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // by lazy - create this only when it is first accessed
    // If weatherApiService is never used, its never created - save memory
    // Once creates, the same instance is reused - no rebuilding
    val weatherApiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            // automatic JSON-to-data-class- conversion when the API
            // returns JSON, Gson maps it to weatherresponse
            .build()
            .create(WeatherApiService::class.java)
            // retrofit generates the real HTTP implementation
        // tells retrofit which interface to implement
    }
}