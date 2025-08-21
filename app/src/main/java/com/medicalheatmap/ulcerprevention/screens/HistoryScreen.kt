package com.medicalheatmap.ulcerprevention.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicalheatmap.ulcerprevention.data.HistoryEntry
import com.medicalheatmap.ulcerprevention.data.SimulatedDataProvider
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen() {
    val dataProvider = remember { SimulatedDataProvider() }
    val historyData = remember { dataProvider.getHistoryData() }
    var selectedTimeRange by remember { mutableStateOf("24h") }
    var selectedZone by remember { mutableStateOf("Todas") }

    val filteredData = remember(selectedTimeRange, selectedZone, historyData) {
        val currentTime = System.currentTimeMillis()
        val timeRange = when (selectedTimeRange) {
            "1h" -> 3600000L
            "6h" -> 21600000L
            "24h" -> 86400000L
            else -> Long.MAX_VALUE
        }

        historyData.filter { entry ->
            val timeCondition = currentTime - entry.timestamp <= timeRange
            val zoneCondition = selectedZone == "Todas" || entry.zoneName == selectedZone
            timeCondition && zoneCondition
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        // Header con controles
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
                    Column {
                        Text(
                            text = "Historial de Presión",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Análisis de datos históricos y tendencias",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = "Analytics",
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${filteredData.size} registros",
                            fontSize = 12.sp,
                            color = Color(0xFF6366F1),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filtros
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Filtro de tiempo
                    FilterChipGroup(
                        options = listOf("1h", "6h", "24h", "Todo"),
                        selectedOption = selectedTimeRange,
                        onSelectionChanged = { selectedTimeRange = it },
                        modifier = Modifier.weight(1f)
                    )

                    // Filtro de zona
                    FilterChipGroup(
                        options = listOf("Todas", "Zona Sacral", "Talón Izquierdo", "Talón Derecho", "Hombro Izquierdo"),
                        selectedOption = selectedZone,
                        onSelectionChanged = { selectedZone = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Lista de eventos históricos
            Card(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Eventos Registrados",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredData.take(20)) { entry ->
                            HistoryEventCard(entry)
                        }
                    }
                }
            }

            // Panel de estadísticas
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Estadísticas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    StatisticsPanel(filteredData)
                }
            }
        }
    }
}

@Composable
fun FilterChipGroup(
    options: List<String>,
    selectedOption: String,
    onSelectionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(options) { option ->
            FilterChip(
                onClick = { onSelectionChanged(option) },
                label = {
                    Text(
                        text = option,
                        fontSize = 12.sp
                    )
                },
                selected = option == selectedOption,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun HistoryEventCard(entry: HistoryEntry) {
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormatter = SimpleDateFormat("dd/MM", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                entry.pressureLevel > 0.8f -> Color(0xFFFEF2F2)
                entry.pressureLevel > 0.6f -> Color(0xFFFFF7ED)
                else -> Color(0xFFF0FDF4)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Indicador de nivel de presión
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = when {
                                entry.pressureLevel > 0.8f -> Color(0xFFDC2626)
                                entry.pressureLevel > 0.6f -> Color(0xFFD97706)
                                else -> Color(0xFF059669)
                            },
                            shape = RoundedCornerShape(6.dp)
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = entry.zoneName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${dateFormatter.format(Date(entry.timestamp))} ${timeFormatter.format(Date(entry.timestamp))}",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )

                        if (entry.coolingActivated) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.AcUnit,
                                contentDescription = "Cooling",
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            // Nivel de presión
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${(entry.pressureLevel * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )

                Text(
                    text = when {
                        entry.pressureLevel > 0.8f -> "Crítico"
                        entry.pressureLevel > 0.6f -> "Alto"
                        entry.pressureLevel > 0.3f -> "Moderado"
                        else -> "Bajo"
                    },
                    fontSize = 9.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun StatisticsPanel(historyData: List<HistoryEntry>) {
    val totalEvents = historyData.size
    val criticalEvents = historyData.count { it.pressureLevel > 0.8f }
    val coolingActivations = historyData.count { it.coolingActivated }
    val avgPressure = if (historyData.isNotEmpty()) {
        historyData.map { it.pressureLevel }.average()
    } else 0.0

    val mostAffectedZone = historyData
        .groupBy { it.zoneName }
        .maxByOrNull { it.value.size }
        ?.key ?: "N/A"

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            title = "Eventos Totales",
            value = totalEvents.toString(),
            icon = Icons.Default.EventNote,
            color = Color(0xFF6366F1)
        )

        StatCard(
            title = "Eventos Críticos",
            value = criticalEvents.toString(),
            icon = Icons.Default.Warning,
            color = Color(0xFFDC2626)
        )

        StatCard(
            title = "Enfriamientos",
            value = coolingActivations.toString(),
            icon = Icons.Default.AcUnit,
            color = Color(0xFF3B82F6)
        )

        StatCard(
            title = "Presión Promedio",
            value = "${(avgPressure * 100).toInt()}%",
            icon = Icons.Default.TrendingUp,
            color = Color(0xFF10B981)
        )

        StatCard(
            title = "Zona Más Afectada",
            value = mostAffectedZone.take(15),
            icon = Icons.Default.Place,
            color = Color(0xFFF59E0B),
            isText = true
        )

        // Gráfico simple de tendencia
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Tendencia Últimas 6h",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val last6Hours = historyData.take(6)
                val trend = if (last6Hours.size >= 2) {
                    val recent = last6Hours.take(3).map { it.pressureLevel }.average()
                    val older = last6Hours.drop(3).map { it.pressureLevel }.average()
                    when {
                        recent > older + 0.1 -> "↗️ Aumentando"
                        recent < older - 0.1 -> "↘️ Disminuyendo"
                        else -> "→ Estable"
                    }
                } else "→ Insuficientes datos"

                Text(
                    text = trend,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isText: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
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
                Icon(
                    icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = value,
                    fontSize = if (isText) 10.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 10.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}