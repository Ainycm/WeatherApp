package com.mdi2.weatherapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdi2.weatherapp.network.AppConstants
import com.mdi2.weatherapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WeatherScreen()
                }
            }
        }
    }
}

@Composable
fun WeatherScreen() {  // a value Compose watches changes for

    var city by remember { mutableStateOf("") }
    var cityResult by remember { mutableStateOf("City: --") }
    var tempResult by remember { mutableStateOf("Temp: -- ") }
    var descResult by remember { mutableStateOf("Description: --") }

    val context = LocalContext.current

    // 1. Add "val scope = rememberCoroutineScope()" near the top of
    //    WeatherScreen(), next to where "val context = ..." already is
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(72.dp)
    ) {

        TextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Enter City Name: ")},
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val trimmedCity = city.trim()
                if (trimmedCity.isEmpty()) {
                    Toast.makeText(context, "Please enter a city name", Toast.LENGTH_SHORT).show()

                } else {
                    // 2. Replace the Toast line below with "scope.launch { }"
                    scope.launch {
                        // 7. Wrap steps 2-6 in try { } catch (e: Exception) { }, showing
                        //    "Network error. Check your connection." in the catch block
                        try {
                            // 3. Inside launch, use "withContext(Dispatchers.IO) { }" for the network call
                            val response = withContext(Dispatchers.IO) {
                                // 4. Call RetrofitClient.weatherApiService.getWeather(trimmedCity, AppConstants.API_KEY, AppConstants.UNITS)
                                RetrofitClient.weatherApiService.getWeather(
                                    trimmedCity,
                                    AppConstants.API_KEY,
                                    AppConstants.UNITS
                                )
                            }

                            // 5. If response.isSuccessful, update cityResult / tempResult / descResult
                            if (response.isSuccessful) {
                                val weather = response.body()
                                if (weather != null) {
                                    cityResult = "City: ${weather.name}"
                                    tempResult = "Temperature: ${weather.main.temp} °C"
                                    descResult = "Description: ${weather.weather[0].description}"
                                }

                            } else {
                                // 6. If NOT successful, show a Toast: "City not found. Check the name and try again."
                                Toast.makeText(
                                    context,
                                    "City not found. Check the name and try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e: Exception) {
                            // 7. Wrap steps 2-6 in try { } catch (e: Exception) { }, showing
                            //    "Network error. Check your connection." in the catch block
                            Toast.makeText(
                                context,
                                "Network error. Check your connection.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) { Text("Get Weather") }

        Text(cityResult, fontSize = 20.sp, modifier = Modifier.padding(top = 24.dp))
        Text(tempResult, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
        Text(descResult, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
    }
}