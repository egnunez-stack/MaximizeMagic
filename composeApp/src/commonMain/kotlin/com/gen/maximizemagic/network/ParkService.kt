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

// --- MODELOS CLIMA ---
@Serializable
data class WeatherResponse(val daily: DailyData? = null)

@Serializable
data class DailyData(
    @SerialName("temperature_2m_max") val maxTemp: List<Double> = emptyList(),
    @SerialName("temperature_2m_min") val minTemp: List<Double> = emptyList()
)

// --- MODELOS THEMEPARKS.WIKI (Para Epic Universe) ---
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

// --- MODELOS QUEUE-TIMES (Parques actuales) ---
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

    // 1. CLIMA DE ORLANDO (Mín/Máx)
    suspend fun getOrlandoForecast(): Pair<Double, Double>? {
        return try {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=28.38&longitude=-81.56&daily=temperature_2m_max,temperature_2m_min&timezone=America%2FNew_York&forecast_days=1"
            val resp = client.get(url).body<WeatherResponse>()
            val daily = resp.daily
            if (daily != null && daily.maxTemp.isNotEmpty()) Pair(daily.minTemp[0], daily.maxTemp[0]) else null
        } catch (e: Exception) { null }
    }

    // 2. DATOS EPIC UNIVERSE (Themeparks.wiki)
    suspend fun getEpicUniverseData(): QueueTimesResponse? {
        return try {
            val url = "https://api.themeparks.wiki/v1/entity/64309322-a96b-4f9e-a0e0-82601705e468/live"
            val resp = client.get(url).body<WikiParkResponse>()
            val rides = resp.liveData.filter { it.entityType == "ATTRACTION" }.map {
                AttractionAlternative(it.id.hashCode(), it.name, it.status == "OPERATING", it.queue?.STANDBY?.waitTime ?: 0)
            }
            QueueTimesResponse(rides = rides)
        } catch (e: Exception) { null }
    }

    // 3. DATOS OTROS PARQUES (Queue-Times)
    suspend fun getParkData(parkId: String): QueueTimesResponse? {
        if (parkId == "epic-uuid") return getEpicUniverseData()
        return try {
            client.get("https://queue-times.com/parks/$parkId/queue_times.json").body<QueueTimesResponse>()
        } catch (e: Exception) { null }
    }
}