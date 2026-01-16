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

// --- MODELOS PARA THEMEPARKS.WIKI (Epic Universe) ---
@Serializable
data class WikiParkResponse(val liveData: List<WikiAttraction> = emptyList())

@Serializable
data class WikiAttraction(
    val id: String,
    val name: String,
    val entityType: String,
    val status: String? = null,
    val queue: WikiQueue? = null
)

@Serializable
data class WikiQueue(val STANDBY: WikiWaitTime? = null)

@Serializable
data class WikiWaitTime(val waitTime: Int? = null)

// --- MODELOS PARA QUEUE-TIMES (Parques actuales) ---
@Serializable
data class QueueTimesResponse(
    val lands: List<Land> = emptyList(),
    val rides: List<AttractionAlternative> = emptyList()
)

@Serializable
data class Land(val name: String = "", val rides: List<AttractionAlternative> = emptyList())

@Serializable
data class AttractionAlternative(
    val id: Int,
    val name: String,
    val is_open: Boolean = false,
    val wait_time: Int = 0,
    val last_updated: String = ""
)

class ParkApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; coerceInputValues = true })
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
     * 2. DATOS EPIC UNIVERSE (Themeparks.wiki)
     */
    suspend fun getEpicUniverseData(): QueueTimesResponse? {
        return try {
            val url = "https://api.themeparks.wiki/v1/entity/64309322-a96b-4f9e-a0e0-82601705e468/live"
            val resp = client.get(url).body<WikiParkResponse>()
            val rides = resp.liveData.filter { it.entityType == "ATTRACTION" }.map {
                AttractionAlternative(
                    id = it.id.hashCode(),
                    name = it.name,
                    is_open = it.status == "OPERATING",
                    wait_time = it.queue?.STANDBY?.waitTime ?: 0
                )
            }
            QueueTimesResponse(rides = rides)
        } catch (e: Exception) { null }
    }

    /**
     * 3. DATOS GENERALES DE PARQUES
     */
    suspend fun getParkData(parkId: String): QueueTimesResponse? {
        // Redirección automática si es el ID de Epic Universe
        if (parkId == "epic-wiki") return getEpicUniverseData()

        return try {
            client.get("https://queue-times.com/parks/$parkId/queue_times.json").body<QueueTimesResponse>()
        } catch (e: Exception) { null }
    }
}