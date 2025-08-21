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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicalheatmap.ulcerprevention.data.PressurePoint
import com.medicalheatmap.ulcerprevention.data.SimulatedDataProvider
import java.util.concurrent.TimeUnit

@Composable
fun DashboardScreen() {
    val dataProvider = remember { SimulatedDataProvider() }
    val pressureData by dataProvider.getPressureDataStream().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        // Header
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
                    Text(
                        text = "Sistema de Prevención de Úlceras",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Circle,
                            contentDescription = "Status",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Activo",
                            fontSize = 14.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Monitoreo en tiempo real de puntos de presión",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Row {
            // Heatmap Principal
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
                    Text(
                        text = "Mapa de Calor Corporal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    HeatMapCanvas(
                        pressurePoints = pressureData,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Panel de Alertas
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
                        text = "Alertas Activas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val criticalZones = pressureData.filter { it.currentPressure > 0.6f }
                        items(criticalZones) { zone ->
                            PressureAlertCard(zone)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeatMapCanvas(
    pressurePoints: List<PressurePoint>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier) {
        // Dibujar silueta del cuerpo humano (simplificada)
        drawBodyOutline()

        // Dibujar puntos de presión
        pressurePoints.forEach { point ->
            val color = getPressureColor(point.currentPressure)
            val radius = if (point.currentPressure > 0.6f) 40f else 25f
            val alpha = if (point.currentPressure > 0.6f) pulseAlpha else 1.0f

            // Dibujar gradiente de presión
            val center = Offset(
                x = size.width * point.x,
                y = size.height * point.y
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = alpha),
                        color.copy(alpha = alpha * 0.3f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // Indicador de enfriamiento activo
            if (point.isBeingCooled) {
                drawCircle(
                    color = Color(0xFF3B82F6).copy(alpha = 0.8f),
                    radius = 8f,
                    center = Offset(center.x + 25f, center.y - 25f)
                )
            }
        }
    }
}

@Composable
fun PressureAlertCard(zone: PressurePoint) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                zone.currentPressure > 0.8f -> Color(0xFFFEF2F2)
                zone.currentPressure > 0.6f -> Color(0xFFFFF7ED)
                else -> Color(0xFFF0FDF4)
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
                        when {
                            zone.currentPressure > 0.8f -> Icons.Default.Error
                            zone.currentPressure > 0.6f -> Icons.Default.Warning
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = "Alert Level",
                        tint = when {
                            zone.currentPressure > 0.8f -> Color(0xFFDC2626)
                            zone.currentPressure > 0.6f -> Color(0xFFD97706)
                            else -> Color(0xFF059669)
                        },
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = zone.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B)
                    )
                }

                if (zone.isBeingCooled) {
                    Icon(
                        Icons.Default.AcUnit,
                        contentDescription = "Cooling Active",
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tiempo: ${formatDuration(zone.accumulatedTime)}",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            LinearProgressIndicator(
                progress = zone.currentPressure,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = getPressureColor(zone.currentPressure),
                trackColor = Color(0xFFE2E8F0)
            )
        }
    }
}

// Funciones auxiliares
fun getPressureColor(pressure: Float): Color {
    return when {
        pressure < 0.3f -> Color(0xFF3B82F6) // Azul
        pressure < 0.5f -> Color(0xFF10B981) // Verde
        pressure < 0.7f -> Color(0xFFF59E0B) // Amarillo
        else -> Color(0xFFDC2626) // Rojo
    }
}

fun formatDuration(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    return "${hours}h ${minutes}m"
}

fun DrawScope.drawBodyOutline() {
    // Silueta simplificada del cuerpo humano
    val bodyColor = Color(0xFFE2E8F0)

    // Cabeza (círculo)
    drawCircle(
        color = bodyColor,
        radius = 30f,
        center = Offset(size.width * 0.5f, size.height * 0.15f)
    )

    // Torso (rectángulo redondeado)
    val torsoWidth = size.width * 0.3f
    val torsoHeight = size.height * 0.4f
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(
            size.width * 0.5f - torsoWidth / 2,
            size.height * 0.2f
        ),
        size = androidx.compose.ui.geometry.Size(torsoWidth, torsoHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(15f)
    )

    // Piernas
    val legWidth = size.width * 0.1f
    val legHeight = size.height * 0.35f

    // Pierna izquierda
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(
            size.width * 0.4f - legWidth / 2,
            size.height * 0.55f
        ),
        size = androidx.compose.ui.geometry.Size(legWidth, legHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f)
    )

    // Pierna derecha
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(
            size.width * 0.6f - legWidth / 2,
            size.height * 0.55f
        ),
        size = androidx.compose.ui.geometry.Size(legWidth, legHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f)
    )
}