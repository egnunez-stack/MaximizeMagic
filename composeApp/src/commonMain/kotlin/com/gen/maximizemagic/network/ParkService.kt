package com.gen.maximizemagic.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
    val is_open: Boolean = false,
    val wait_time: Int = 0,
    val last_updated: String = "" // Guardamos la hora de actualización
)

class ParkApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; coerceInputValues = true })
        }
    }

    // AHORA: Devuelve la respuesta completa del parque
    suspend fun getParkData(parkId: String): QueueTimesResponse? {
        return try {
            val url = "https://queue-times.com/parks/$parkId/queue_times.json"
            client.get(url).body<QueueTimesResponse>()
        } catch (e: Exception) {
            null
        }
    }
}
