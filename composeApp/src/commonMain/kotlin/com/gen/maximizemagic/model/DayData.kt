package com.gen.maximizemagic.model

import kotlinx.serialization.Serializable

@Serializable
data class DayData(
    val date: String,
    val tempMax: Double,
    val tempMin: Double,
    val condition: String
    // ... other properties you expect
)

