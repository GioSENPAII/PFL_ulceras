package com.medicalheatmap.ulcerprevention.data

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class PressurePoint(
    val id: String,
    val name: String,
    val x: Float, // Posición X en porcentaje (0.0 - 1.0)
    val y: Float, // Posición Y en porcentaje (0.0 - 1.0)
    val currentPressure: Float, // 0.0 - 1.0
    val accumulatedTime: Long, // Tiempo en milisegundos
    val isBeingCooled: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class PredictionPoint(
    val zoneId: String,
    val zoneName: String,
    val probability: Float, // 0.0 - 1.0
    val timeToIncrease: Int, // minutos
    val x: Float,
    val y: Float
)

data class HistoryEntry(
    val timestamp: Long,
    val zoneId: String,
    val zoneName: String,
    val pressureLevel: Float,
    val coolingActivated: Boolean
)