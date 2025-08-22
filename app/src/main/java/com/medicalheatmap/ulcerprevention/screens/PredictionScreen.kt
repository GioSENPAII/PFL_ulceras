// Archivo: screens/PredictionScreen.kt
package com.medicalheatmap.ulcerprevention.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicalheatmap.ulcerprevention.data.PredictionPoint
import com.medicalheatmap.ulcerprevention.data.SimulatedDataProvider

@Composable
fun PredictionScreen() {
    val dataProvider = remember { SimulatedDataProvider() }
    val predictions by dataProvider.getPredictionsStream().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        // Header con información de IA
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = "IA",
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Predicción de Presión por IA",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Análisis predictivo en tiempo real",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "AI Status",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "IA Activa",
                                fontSize = 12.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Row {
            // Mapa de Predicciones
            Card(
                modifier = Modifier
                    .weight(2f)
                    .height(500.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Zonas de Riesgo Futuro",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )

                        // Leyenda de colores
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LegendItem(Color(0xFFFF8C00), "Predicción IA")
                            Spacer(modifier = Modifier.width(12.dp))
                            LegendItem(Color(0xFF3B82F6), "Enfriamiento")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    PredictionCanvas(
                        predictions = predictions,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Panel de Predicciones Detalladas
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(500.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Análisis Predictivo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(predictions) { prediction ->
                            PredictionCard(prediction)
                        }

                        item {
                            AIInsightCard()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PredictionCanvas(
    predictions: List<PredictionPoint>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        )
    )

    val strokePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier) {
        // Dibujar silueta del cuerpo (misma función que en DashboardScreen)
        drawBodyOutline()

        // Dibujar predicciones de IA
        predictions.forEach { prediction ->
            val center = Offset(
                x = size.width * prediction.x,
                y = size.height * prediction.y
            )

            val predictionColor = Color(0xFFFF8C00) // Naranja para predicción
            val radius = 35f + (prediction.probability * 20f)

            // Círculo de predicción con animación pulsante
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        predictionColor.copy(alpha = pulseAlpha * prediction.probability),
                        predictionColor.copy(alpha = pulseAlpha * prediction.probability * 0.3f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // Círculo exterior con línea punteada animada
            drawCircle(
                color = predictionColor,
                radius = radius + 10f,
                center = center,
                style = Stroke(
                    width = 3f,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(10f, 10f),
                        phase = strokePhase
                    )
                )
            )

            // Indicador de enfriamiento anticipado
            if (prediction.probability > 0.6f) {
                drawCircle(
                    color = Color(0xFF3B82F6).copy(alpha = 0.9f),
                    radius = 12f,
                    center = Offset(center.x + 30f, center.y - 30f)
                )

                // Icono de enfriamiento (simplificado)
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = Offset(center.x + 30f, center.y - 30f)
                )
            }
        }

        // Dibujar líneas de conexión entre predicciones relacionadas
        if (predictions.size > 1) {
            for (i in 0 until predictions.size - 1) {
                val start = Offset(
                    size.width * predictions[i].x,
                    size.height * predictions[i].y
                )
                val end = Offset(
                    size.width * predictions[i + 1].x,
                    size.height * predictions[i + 1].y
                )

                drawLine(
                    color = Color(0xFFFF8C00).copy(alpha = 0.3f),
                    start = start,
                    end = end,
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                )
            }
        }
    }
}

@Composable
fun PredictionCard(prediction: PredictionPoint) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                prediction.probability > 0.7f -> Color(0xFFFFF7ED)
                prediction.probability > 0.5f -> Color(0xFFFFFBEB)
                else -> Color(0xFFF8FAFC)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = "Prediction",
                        tint = Color(0xFFFF8C00),
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = prediction.zoneName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B)
                    )
                }

                if (prediction.probability > 0.6f) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AcUnit,
                            contentDescription = "Cooling Scheduled",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Scheduled",
                            fontSize = 10.sp,
                            color = Color(0xFF3B82F6),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Probability",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "${(prediction.probability * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Estimated time",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "${prediction.timeToIncrease} min",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = prediction.probability,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(2.dp)),
                color = Color(0xFFFF8C00),
                trackColor = Color(0xFFE2E8F0)
            )
        }
    }
}

@Composable
fun AIInsightCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = "AI Insight",
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "AI Recommendation",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Based on current pressure patterns, position change recommended in next 20 minutes to prevent sacral zone ulcers.",
                fontSize = 11.sp,
                color = Color(0xFF4B5563),
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Confianza: 87%",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "High precision",
                        fontSize = 9.sp,
                        color = Color(0xFF059669),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF64748B)
        )
    }
}