package com.medicalheatmap.ulcerprevention.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicalheatmap.ulcerprevention.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToSetup: () -> Unit) {
    val alphaAnimation by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 2000),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        delay(3000) // Show splash for 3 seconds
        onNavigateToSetup()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3B82F6)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alphaAnimation)
        ) {
            // Logo placeholder - reemplaza con tu logo real
            Image(
                painter = painterResource(id = R.drawable.ic_medical_monitoring), // Temporal
                contentDescription = stringResource(R.string.app_logo_description),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.app_name),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Smart Pressure Monitoring",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}