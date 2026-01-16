package com.gen.maximizemagic.model

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class SettingsManager {
    // Esta línea funcionará siempre que tengas "multiplatform-settings-no-arg" en build.gradle.kts
    private val settings: Settings = Settings()

    // --- PROPIEDADES PERSISTENTES ---

    // Dirección completa del hogar
    var homeAddress: String
        get() = settings.getString("home_full_address", "")
        set(value) { settings.set("home_full_address", value) }

    // Idioma
    var language: String
        get() = settings.getString("app_language", "es")
        set(value) { settings.set("app_language", value) }

    // Vuelo de llegada (RECUPERADO)
    var arrivalFlight: String
        get() = settings.getString("arrival_flight", "")
        set(value) { settings.set("arrival_flight", value) }

    // Vuelo de partida (RECUPERADO)
    var departureFlight: String
        get() = settings.getString("departure_flight", "")
        set(value) { settings.set("departure_flight", value) }
}