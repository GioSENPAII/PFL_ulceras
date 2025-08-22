package com.medicalheatmap.ulcerprevention.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medicalheatmap.ulcerprevention.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(onNavigateToConnection: (String) -> Unit) {
    var selectedOption by remember { mutableStateOf("manual") } // "qr" or "manual"
    var serialNumber by remember { mutableStateOf("") }
    var showQRScanner by remember { mutableStateOf(false) }

    if (showQRScanner) {
        QRScannerScreen(
            onQRCodeScanned = { scannedCode ->
                serialNumber = scannedCode
                showQRScanner = false
                selectedOption = "manual"
            },
            onDismiss = { showQRScanner = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = stringResource(R.string.setup_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.setup_subtitle),
                fontSize = 16.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // QR Scanner Option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedOption == "qr") Color(0xFFEBF8FF) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                onClick = {
                    selectedOption = "qr"
                    showQRScanner = true
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = "QR Scanner",
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.scan_qr_option),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Scan the QR code on your mattress cover",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Manual Entry Option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedOption == "manual") Color(0xFFEBF8FF) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                onClick = { selectedOption = "manual" }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Manual Entry",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = stringResource(R.string.manual_entry_option),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Enter the serial number manually",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    if (selectedOption == "manual") {
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = serialNumber,
                            onValueChange = { serialNumber = it.uppercase() },
                            label = { Text(stringResource(R.string.serial_number_label)) },
                            placeholder = { Text("e.g., PG-2024-001234") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            leadingIcon = {
                                Icon(Icons.Default.Numbers, contentDescription = null)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Connect Button
            Button(
                onClick = {
                    if (serialNumber.isNotBlank()) {
                        onNavigateToConnection(serialNumber)
                    }
                },
                enabled = serialNumber.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.connect_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}