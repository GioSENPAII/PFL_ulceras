package com.medicalheatmap.ulcerprevention.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlin.random.Random

class SimulatedDataProvider {

    private val bodyZones = listOf(
        PressurePoint("sacral", "Zona Sacral", 0.5f, 0.65f, 0.7f, 8100000L, true),
        PressurePoint("heel_left", "Talón Izquierdo", 0.3f, 0.9f, 0.4f, 3600000L, false),
        PressurePoint("heel_right", "Talón Derecho", 0.7f, 0.9f, 0.8f, 9900000L, true),
        PressurePoint("shoulder_left", "Hombro Izquierdo", 0.2f, 0.25f, 0.3f, 1800000L, false),
        PressurePoint("shoulder_right", "Hombro Derecho", 0.8f, 0.25f, 0.5f, 5400000L, false),
        PressurePoint("elbow_left", "Codo Izquierdo", 0.15f, 0.4f, 0.2f, 900000L, false),
        PressurePoint("elbow_right", "Codo Derecho", 0.85f, 0.4f, 0.6f, 7200000L, true),
        PressurePoint("hip_left", "Cadera Izquierda", 0.35f, 0.55f, 0.4f, 2700000L, false),
        PressurePoint("hip_right", "Cadera Derecha", 0.65f, 0.55f, 0.3f, 1800000L, false)
    )

    fun getPressureDataStream(): Flow<List<PressurePoint>> = flow {
        while (true) {
            val updatedZones = bodyZones.map { zone ->
                val pressureVariation = Random.nextFloat() * 0.1f - 0.05f // ±5%
                val newPressure = (zone.currentPressure + pressureVariation).coerceIn(0.0f, 1.0f)
                val timeIncrement = Random.nextLong(1000, 3000) // 1-3 segundos
                val shouldCool = newPressure > 0.6f && zone.accumulatedTime > 7200000L // 2 horas

                zone.copy(
                    currentPressure = newPressure,
                    accumulatedTime = zone.accumulatedTime + timeIncrement,
                    isBeingCooled = shouldCool,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            emit(updatedZones)
            delay(2000) // Actualización cada 2 segundos
        }
    }

    fun getPredictionsStream(): Flow<List<PredictionPoint>> = flow {
        while (true) {
            val predictions = listOf(
                PredictionPoint("ankle_left", "Tobillo Izquierdo", 0.75f, 25, 0.25f, 0.85f),
                PredictionPoint("knee_right", "Rodilla Derecha", 0.68f, 35, 0.75f, 0.7f),
                PredictionPoint("wrist_left", "Muñeca Izquierda", 0.55f, 45, 0.1f, 0.35f)
            )

            val updatedPredictions = predictions.map { prediction ->
                val probVariation = Random.nextFloat() * 0.1f - 0.05f
                val timeVariation = Random.nextInt(-5, 5)

                prediction.copy(
                    probability = (prediction.probability + probVariation).coerceIn(0.0f, 1.0f),
                    timeToIncrease = (prediction.timeToIncrease + timeVariation).coerceAtLeast(5)
                )
            }

            emit(updatedPredictions)
            delay(5000) // Actualización cada 5 segundos
        }
    }

    fun getHistoryData(): List<HistoryEntry> {
        val history = mutableListOf<HistoryEntry>()
        val currentTime = System.currentTimeMillis()

        bodyZones.forEach { zone ->
            for (i in 1..24) { // 24 horas de historial
                val timestamp = currentTime - (i * 3600000L) // Cada hora hacia atrás
                val pressure = Random.nextFloat()
                val cooling = pressure > 0.6f

                history.add(
                    HistoryEntry(
                        timestamp = timestamp,
                        zoneId = zone.id,
                        zoneName = zone.name,
                        pressureLevel = pressure,
                        coolingActivated = cooling
                    )
                )
            }
        }

        return history.sortedByDescending { it.timestamp }
    }
}