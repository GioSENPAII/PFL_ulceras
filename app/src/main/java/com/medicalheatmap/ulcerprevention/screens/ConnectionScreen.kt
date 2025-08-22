// Archivo: app/src/main/java/com/medicalheatmap/ulcerprevention/screens/ConnectionScreen.kt
package com.medicalheatmap.ulcerprevention.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicalheatmap.ulcerprevention.R
import kotlinx.coroutines.delay

@Composable
fun ConnectionScreen(
    serialNumber: String,
    onNavigateToDashboard: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var isConnected by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf("Initializing...") }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        // Simulate detailed connection progress with steps
        val steps = listOf(
            "Initializing connection..." to 0.1f,
            "Scanning for device..." to 0.25f,
            "Device found: $serialNumber" to 0.4f,
            "Establishing secure connection..." to 0.6f,
            "Retrieving mattress cover data..." to 0.8f,
            "Syncing pressure sensors..." to 0.9f,
            "Connection established!" to 1.0f
        )

        steps.forEach { (step, targetProgress) ->
            currentStep = step
            // Animate to target progress
            val startProgress = progress
            val duration = ((targetProgress - startProgress) * 2000).toInt() // 2 seconds total
            val steps = 20
            repeat(steps) {
                delay(duration.toLong() / steps)
                progress = startProgress + (targetProgress - startProgress) * (it + 1) / steps
            }
            delay(200) // Small pause between steps
        }

        isConnected = true
        delay(1500) // Show success for 1.5 seconds
        onNavigateToDashboard()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon with animation
                if (isConnected) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(80.dp)
                    )
                } else {
                    // Animated WiFi icon
                    val infiniteTransition = rememberInfiniteTransition()
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "wifi_animation"
                    )

                    Icon(
                        Icons.Default.Wifi,
                        contentDescription = "Connecting",
                        tint = Color(0xFF3B82F6).copy(alpha = alpha),
                        modifier = Modifier.size(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = if (isConnected)
                        stringResource(R.string.connection_successful)
                    else
                        stringResource(R.string.connecting_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Serial number info
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF1F5F9)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Numbers,
                            contentDescription = "Serial",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Device: $serialNumber",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Current step description
                Text(
                    text = if (isConnected)
                        "Redirecting to dashboard..."
                    else
                        currentStep,
                    fontSize = 16.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Progress section
                if (!isConnected) {
                    LinearProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF3B82F6),
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = "Time",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "~${((1 - animatedProgress) * 2).toInt()}s",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else {
                    // Success animation
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Speed,
                            contentDescription = "Fast",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Connected in 2.0s",
                            fontSize = 14.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Additional info card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isConnected)
                            Color(0xFFF0FDF4)
                        else
                            Color(0xFFF8FAFC)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (isConnected) Icons.Default.Security else Icons.Default.Info,
                                contentDescription = "Info",
                                tint = if (isConnected) Color(0xFF059669) else Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isConnected)
                                    "Secure connection established"
                                else
                                    "Establishing secure connection...",
                                fontSize = 12.sp,
                                color = if (isConnected) Color(0xFF065F46) else Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (isConnected) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "• 9 pressure sensors detected\n• Real-time monitoring active\n• AI predictions enabled",
                                fontSize = 11.sp,
                                color = Color(0xFF065F46),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        if (!isConnected) {
            Spacer(modifier = Modifier.height(24.dp))

            // Cancel button (optional)
            OutlinedButton(
                onClick = { /* Handle cancel if needed */ },
                modifier = Modifier.fillMaxWidth(0.5f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF64748B)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 14.sp
                )
            }
        }
    }
}