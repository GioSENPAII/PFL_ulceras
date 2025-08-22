package com.medicalheatmap.ulcerprevention.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicalheatmap.ulcerprevention.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(
    onQRCodeScanned: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Simulate QR code scanning after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        onQRCodeScanned("PG-2024-DEMO123")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera preview placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A)),
            contentAlignment = Alignment.Center
        ) {
            // QR Scanner overlay
            QRScannerOverlay()
        }

        // Top bar
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.qr_scanner_title),
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Bottom instruction
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            )
        ) {
            Text(
                text = "Point your camera at the QR code on your mattress cover",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun QRScannerOverlay() {
    Canvas(
        modifier = Modifier.size(250.dp)
    ) {
        val strokeWidth = 4.dp.toPx()
        val cornerLength = 30.dp.toPx()

        // Draw corner brackets
        val corners = listOf(
            // Top-left
            Pair(Offset(0f, 0f), Offset(cornerLength, 0f)),
            Pair(Offset(0f, 0f), Offset(0f, cornerLength)),
            // Top-right
            Pair(Offset(size.width, 0f), Offset(size.width, cornerLength)),
            // Bottom-left
            Pair(Offset(0f, size.height - cornerLength), Offset(0f, size.height)),
            Pair(Offset(0f, size.height), Offset(cornerLength, size.height)),
            // Bottom-right
            Pair(Offset(size.width - cornerLength, size.height), Offset(size.width, size.height)),
            Pair(Offset(size.width, size.height - cornerLength), Offset(size.width, size.height))
        )

        corners.forEach { (start, end) ->
            drawLine(
                color = Color.White,
                start = start,
                end = end,
                strokeWidth = strokeWidth
            )
        }

        // Draw scanning line (animated)
        drawLine(
            color = Color.Red,
            start = Offset(0f, size.height * 0.5f),
            end = Offset(size.width, size.height * 0.5f),
            strokeWidth = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
        )
    }
}