package com.mdi2.weatherapp.network

import com.mdi2.weatherapp.data.Weather
import com.mdi2.weatherapp.data.WeatherResponse
import retrofit2.Response // wraps the result and give us isSuccessful + error codes
import retrofit2.http.GET // annotation for HTTP GET request
import retrofit2.http.Query // annotation for URL query parameters (?key = value)


// a contract / blueprint
// Wee write WHAT calls we want, retrofit generates HOW they actually work
interface WeatherApiService {

    // @GET = make an HTTP GET request to: WEATHER_BASE_URL + "data/2.5/weather"
    // Full URL example: https://api.openweathermap.org/data/2.5/weather?q=London&appid=KEY&units=metric
    @GET("data/2.5/weather")

    suspend fun getWeather(
        @Query("q")     city: String,  // city we search
        @Query("appid") apiKey: String, // adds your api key
        @Query("units") units: String // adds units
    ): Response<WeatherResponse>


}