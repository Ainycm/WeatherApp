package com.mdi2.weatherapp.network

// "object" in Kotlin = a singleton class
// A singleton means there is exactly ONE instance in the whole app
// You never write: val c = AppConstants() -- you just use: AppConstants.API_KEY
// Perfect for constants because you never want two different copies of your API key
object AppConstants{
    // Base URL = the root address of the API
    // Every call starts with this, then adds the specific endpoint path
    // IMPORTANT: must end with "/" retrofit require this
    const val WEATHER_BASE_URL = "https://api.openweathermap.org/"

    // MY API KEY -
    const val API_KEY = "8249565515db2f62e9585e9ecd56dcc5"

    // UNITs
    const val UNITS = "metric"

    // NEW for Class 3: Paste your APIDog Cloud Mock base URL here
    // Example: "https://mock.apidog.com/m1/123456-0-default/"
    // IMPORTANT: must end with "/" -- Retrofit requires it
    const val FEEDBACK_BASE_URL = "https://mock.apidog.com/m1/1408997-1419504-default/"
}
