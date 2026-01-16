package com.gen.maximizemagic.model

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class SettingsManager {
    private val settings: Settings = Settings()

    // --- HOGAR ---
    var homeStreet: String
        get() = settings.getString("home_street", "")
        set(value) { settings.set("home_street", value) }

    var homeNumber: String
        get() = settings.getString("home_number", "")
        set(value) { settings.set("home_number", value) }

    var homeCity: String
        get() = settings.getString("home_city", "")
        set(value) { settings.set("home_city", value) }

    // --- VUELO IDA ---
    var arrivalFlight: String
        get() = settings.getString("arr_flight", "")
        set(value) { settings.set("arr_flight", value) }

    var arrivalDate: String
        get() = settings.getString("arr_date", "")
        set(value) { settings.set("arr_date", value) }

    var arrivalTime: String
        get() = settings.getString("arr_time", "")
        set(value) { settings.set("arr_time", value) }

    // --- VUELO VUELTA ---
    var departureFlight: String
        get() = settings.getString("dep_flight", "")
        set(value) { settings.set("dep_flight", value) }

    var departureDate: String
        get() = settings.getString("dep_date", "")
        set(value) { settings.set("dep_date", value) }

    var departureTime: String
        get() = settings.getString("dep_time", "")
        set(value) { settings.set("dep_time", value) }

    // --- IDIOMA ---
    var language: String
        get() = settings.getString("app_language", "es")
        set(value) { settings.set("app_language", value) }
}