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
    val wait_time: Int = 0
)

class ParkApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    suspend fun getAttractions(parkId: String): List<AttractionAlternative> {
        if (parkId.isEmpty()) return emptyList()

        return try {
            val url = "https://queue-times.com/parks/$parkId/queue_times.json"
            val response = client.get(url).body<QueueTimesResponse>()

            // Combinamos todas las atracciones posibles
            val allRides = mutableListOf<AttractionAlternative>()
            allRides.addAll(response.rides)

            response.lands.forEach { land ->
                allRides.addAll(land.rides)
            }

            // Filtro importante: Quitamos elementos que no tengan nombre o sean duplicados
            // Algunos parques reportan "Entradas" o "Servicios" como rides con wait_time 0
            allRides.filter { it.name.isNotBlank() }
                .distinctBy { it.id }
                .sortedBy { it.name }

        } catch (e: Exception) {
            println("DEBUG: Error cargando parkId $parkId -> ${e.message}")
            emptyList()
        }
    }
}
