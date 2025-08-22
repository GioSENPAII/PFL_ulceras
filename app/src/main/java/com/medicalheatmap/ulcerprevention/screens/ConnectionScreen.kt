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
    var isComplete by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        // Simulate connection process
        for (i in 1..10) {
            delay(200)
            progress = i / 10f
        }
        delay(500)
        isComplete = true
        delay(1000)
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
                if (isComplete) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.connection_successful),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                } else {
                    CircularProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier.size(64.dp),
                        strokeWidth = 6.dp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.connecting_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.retrieving_data),
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Device Serial Number",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = serialNumber,
                            fontSize = 16.sp,
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (!isComplete) {
                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF3B82F6),
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
} - cornerLength, 0f), Offset(size.width, 0f)),
Pair(Offset(size.width