package com.medicalheatmap.ulcerprevention.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.*

data class HumanBodyPoint(
    val x: Float,
    val y: Float,
    val pressure: Float,
    val influenceRadius: Float = 0.15f
)

class HeatmapAlgorithm {

    companion object {
        // Algoritmo de interpolación bilineal para heatmap
        fun calculateHeatIntensity(
            position: Offset,
            pressurePoints: List<HumanBodyPoint>,
            canvasSize: Size
        ): Float {
            var totalInfluence = 0f
            var totalWeight = 0f

            pressurePoints.forEach { point ->
                val pointPos = Offset(
                    canvasSize.width * point.x,
                    canvasSize.height * point.y
                )

                val distance = sqrt(
                    (position.x - pointPos.x).pow(2) +
                            (position.y - pointPos.y).pow(2)
                )

                val influenceDistance = canvasSize.minDimension * point.influenceRadius

                if (distance < influenceDistance) {
                    // Función de peso inversa a la distancia con suavizado gaussiano
                    val weight = exp(-(distance.pow(2)) / (2 * (influenceDistance / 3).pow(2)))
                    totalInfluence += point.pressure * weight
                    totalWeight += weight
                }
            }

            return if (totalWeight > 0) totalInfluence / totalWeight else 0f
        }

        // Mapa de gradiente de colores más realista
        fun getPressureColor(intensity: Float): Color {
            return when {
                intensity < 0.1f -> Color(0xFF1E3A8A) // Azul muy oscuro (sin presión)
                intensity < 0.25f -> Color(0xFF3B82F6) // Azul medio
                intensity < 0.4f -> Color(0xFF10B981) // Verde
                intensity < 0.55f -> Color(0xFF84CC16) // Verde lima
                intensity < 0.7f -> Color(0xFFF59E0B) // Amarillo
                intensity < 0.85f -> Color(0xFFEF4444) // Rojo
                else -> Color(0xFFDC2626) // Rojo crítico
            }
        }
    }
}