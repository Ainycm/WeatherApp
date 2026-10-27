package com.mdi2.weatherapp.data

// "data" sub-package -- all data model files live here

// data class = a Kotlin class designed only to hold data
// Kotlin auto-generates equals(), hashCode(), toString(), and copy() for free
// You never need to write: "if (a.name == b.name && a.main == b.main ...)" -- data class handles it
data class WeatherResponse(
    val name: String,            // city name = matches "name" key in JSON
    val main: Main,              // nested obj. - contains the temp and humidity
    val weather: List<Weather>,  // array of weather conditions
    val wind: Wind               // nested obj -- contains wind speed
)
 //maps to the main inside Json
 data class Main(
     val temp: Double, // current temperature
     val humidity: Int // humidity as percentage
 )

data class Weather(
    val description: String // "clear sky", "cloudy", etc...
)

data class Wind(
    val speed: Double // wind speed in meters per second
)