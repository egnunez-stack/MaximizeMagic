package com.gen.maximizemagic.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// --- MODELOS PARA EL CLIMA (WeatherAPI Forecast) ---
@Serializable
data class WeatherResponse(
    val current: CurrentWeather? = null,
    val forecast: ForecastData? = null
)

@Serializable
data class CurrentWeather(
    @SerialName("temp_c") val tempC: Double,
    val condition: WeatherCondition? = null
)

@Serializable
data class WeatherCondition(
    val text: String = ""
)

@Serializable
data class ForecastData(
    @SerialName("forecastday") val forecastDay: List<ForecastDay> = emptyList()
)

@Serializable
data class ForecastDay(
    val day: DayDetails
)

@Serializable
data class DayDetails(
    @SerialName("maxtemp_c") val maxTemp: Double,
    @SerialName("mintemp_c") val minTemp: Double,
    @SerialName("daily_chance_of_rain") val rainChance: Int
)

/**
 * Clase de ayuda para pasar todos los datos del clima a la UI de forma limpia
 */
data class OrlandoWeather(
    val currentTemp: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val rainChance: Int,
    val conditionText: String
)

// --- MODELOS PARA QUEUE-TIMES (API UNIFICADA) ---
@Serializable
data class QueueTimesResponse(
    val lands: List<Land> = emptyList(),
    val rides: List<AttractionAlternative> = emptyList()
)

@Serializable
data class Land(
    val name: String = "",
    val rides: List<AttractionAlternative> = emptyList()
)

@Serializable
data class AttractionAlternative(
    val id: Int,
    val name: String,
    @SerialName("is_open") val is_open: Boolean = false,
    @SerialName("wait_time") val wait_time: Int = 0,
    @SerialName("last_updated") val last_updated: String = ""
)

class ParkApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 15000
        }
    }

    /**
     * 1. CLIMA DE ORLANDO COMPLETO (Actual, Mín, Máx, Lluvia)
     */
    suspend fun getOrlandoFullWeather(): OrlandoWeather? {
        return try {
            val key = "6cc733d2d5cd4230a95190322261601"
            val url = "https://api.weatherapi.com/v1/forecast.json?key=$key&q=Orlando&days=1&aqi=no&alerts=no"

            val resp = client.get(url).body<WeatherResponse>()
            val current = resp.current
            val dayData = resp.forecast?.forecastDay?.firstOrNull()?.day

            if (current != null && dayData != null) {
                OrlandoWeather(
                    currentTemp = current.tempC,
                    minTemp = dayData.minTemp,
                    maxTemp = dayData.maxTemp,
                    rainChance = dayData.rainChance,
                    conditionText = current.condition?.text ?: ""
                )
            } else null
        } catch (e: Exception) {
            println("#MaximizeMagic: Error Clima -> ${e.message}")
            null
        }
    }

    /**
     * 2. DATOS EPIC UNIVERSE (Queue-Times.com ID 334)
     * Epic Universe usa una estructura basada en LANDS.
     */
    suspend fun getEpicUniverseData(): QueueTimesResponse? {
        return try {
            // ID 334 es el asignado por Queue-Times a Universal Epic Universe
            val url = "https://queue-times.com/parks/334/queue_times.json"
            client.get(url).body<QueueTimesResponse>()
        } catch (e: Exception) {
            println("#MaximizeMagic: Error Epic Universe -> ${e.message}")
            null
        }
    }

    /**
     * 3. DATOS GENERALES DE PARQUES (Queue-Times.com)
     */
    suspend fun getParkData(parkId: String): QueueTimesResponse? {
        // Redirección automática si es el ID de Epic Universe definido en App.kt
        if (parkId == "epic-wiki" || parkId == "334") {
            return getEpicUniverseData()
        }

        return try {
            val url = "https://queue-times.com/parks/$parkId/queue_times.json"
            client.get(url).body<QueueTimesResponse>()
        } catch (e: Exception) {
            println("#MaximizeMagic: Error ParkData ($parkId) -> ${e.message}")
            null
        }
    }
}