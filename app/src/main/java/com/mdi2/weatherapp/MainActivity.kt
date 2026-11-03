package com.mdi2.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Class 1
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import com.mdi2.weatherapp.network.AppConstants
import com.mdi2.weatherapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
// Class 2
import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
// Class 3
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import com.mdi2.weatherapp.data.FeedbackRequest
// Class 4
import androidx.compose.material3.OutlinedButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(this) // must run before any service is used
        setContent {
            MaterialTheme {
                WeatherScreen()
            }
        }
    }
}

@Composable
fun WeatherScreen() {
    var city by remember { mutableStateOf("") }
    var cityResult by remember { mutableStateOf("City: --") }
    var tempResult by remember { mutableStateOf("Temperature: --") }
    var descResult by remember { mutableStateOf("Description: --") }
    var windResult by remember { mutableStateOf("Wind Speed: --") }
    var humidityResult by remember { mutableStateOf("Humidity: --") }
    var isLoading by remember { mutableStateOf(false) }

    var currentCity by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(3) }
    var comment by remember { mutableStateOf("") }
    var feedbackResult by remember { mutableStateOf("") }
    var feedbackLoading by remember { mutableStateOf(false) }

    var recentSearches by remember { mutableStateOf(listOf<String>()) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // verticalScroll lets the whole form be visible on small screens
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Enter city name") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            enabled = !isLoading, // disables the button mid-request
            onClick = {
                val trimmedCity = city.trim()
                if (trimmedCity.isEmpty()) {
                    Toast.makeText(context, "Please enter a city name", Toast.LENGTH_SHORT).show()
                } else {
                    isLoading = true
                    scope.launch {
                        try {
                            val response = withContext(Dispatchers.IO) {
                                RetrofitClient.weatherApiService.getWeather(
                                    city = trimmedCity,
                                    apiKey = AppConstants.API_KEY,
                                    units = AppConstants.UNITS
                                )
                            }
                            Log.d("WeatherApp", "Request URL: ${response.raw().request.url}")
                            Log.d("WeatherApp", "Response code: ${response.code()}")

                            if (response.isSuccessful) {
                                val weather = response.body()
                                if (weather != null) {
                                    cityResult = "City: ${weather.name}"
                                    tempResult = "Temperature: ${weather.main.temp} °C"
                                    descResult = "Description: ${weather.weather[0].description}"
                                    windResult = "Wind Speed: ${weather.wind.speed} m/s"
                                    humidityResult = "Humidity: ${weather.main.humidity}%"

                                    currentCity = trimmedCity // remember this city so feedback can POST

                                    // newest city first, no duplicates, keep only 3
                                    recentSearches = (listOf(weather.name) +
                                            recentSearches.filter { it != weather.name }).take(3)
                                }
                            } else {
                                // one message per error code
                                when (response.code()) {
                                    404 -> Toast.makeText(context, "City not found. Check the name and try again.", Toast.LENGTH_SHORT).show()
                                    401 -> Toast.makeText(context, "Invalid API key. Check AppConstants.kt.", Toast.LENGTH_SHORT).show()
                                    else -> Toast.makeText(context, "Something went wrong (code ${response.code()}).", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("WeatherApp", "Weather error: ${e.message}", e)
                            Toast.makeText(context, "Network error. Check your connection.", Toast.LENGTH_SHORT).show()
                        } finally { // always runs: success, HTTP error, or exception
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(if (isLoading) "Loading..." else "Get Weather")
        }
        Text(cityResult, fontSize = 20.sp, modifier = Modifier.padding(top = 24.dp))
        Text(tempResult, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
        Text(descResult, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
        Text(windResult, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
        Text(humidityResult, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))

        // Cache control + recent searches
        OutlinedButton(
            onClick = {
                RetrofitClient.clearCache()
                Toast.makeText(context, "Cache Cleared", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Clear Cache")
        }

        if (recentSearches.isNotEmpty()) {
            Text("Recent Searches", fontSize = 14.sp, modifier = Modifier.padding(top = 16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 4.dp)
            ) {
                recentSearches.forEach { recentCity ->
                    AssistChip(
                        onClick = { city = recentCity }, // tap a chip to fill the search field
                        label = { Text(recentCity) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(top = 24.dp, bottom = 16.dp))

        Text("How do you feel about today's weather?", fontSize = 16.sp)

        // rating system
        Slider(
            value = rating.toFloat(),
            onValueChange = { rating = it.toInt() },
            valueRange = 1f..5f,
            steps = 3
        )

        Text("Rating: $rating/5") // slider has no built-in label

        TextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Leave a comment about the weather...") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Button(
            enabled = !feedbackLoading,
            onClick = {
                if (currentCity.isEmpty()) {
                    Toast.makeText(context, "Please fetch weather for a city first", Toast.LENGTH_SHORT).show()
                } else if (comment.isBlank()) {
                    Toast.makeText(context, "Please leave a comment", Toast.LENGTH_SHORT).show()
                } else {
                    feedbackLoading = true
                    scope.launch {
                        try {
                            val request = FeedbackRequest(city = currentCity, rating = rating, comment = comment)
                            val response = withContext(Dispatchers.IO) {
                                RetrofitClient.feedbackApiService.submitFeedback(request)
                            }
                            if (response.isSuccessful) {
                                feedbackResult = "Feedback submitted successfully!"
                                comment = "" // clears the TextField
                                rating = 3   // resets the Slider to the middle
                            } else {
                                feedbackResult = "Failed to submit feedback. Try again."
                            }
                        } catch (e: Exception) {
                            Log.e("WeatherApp", "Feedback error: ${e.message}", e)
                            feedbackResult = "Error submitting feedback. Check your connection."
                        } finally {
                            feedbackLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(if (feedbackLoading) "Submitting..." else "Submit Feedback")
        }
        Text(feedbackResult, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
    }
}