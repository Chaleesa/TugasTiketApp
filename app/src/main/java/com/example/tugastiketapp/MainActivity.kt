package com.example.tugastiketapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TicketScreenParent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreenParent() {
    var nama by remember { mutableStateOf("") }
    var jumlahTiket by remember { mutableStateOf(1) }
    val hargaPerTiket = 50000
    val hargaTotal = jumlahTiket * hargaPerTiket

    var statusMessage by remember { mutableStateOf("") }
    var statusType by remember { mutableStateOf(StatusType.IDLE) }
    var isProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            statusType = StatusType.PROCESSING
            statusMessage = "Status : Memproses pesanan........"
            delay(5000) // Delay 5 detik
            statusType = StatusType.SUCCESS
            statusMessage = "Status : Tiket telah dipesan"
            isProcessing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pemesanan Tiket", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        TicketScreenChild(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            nama = nama,
            onNamaChange = { nama = it },
            jumlahTiket = jumlahTiket,
            onJumlahChange = { jumlahTiket = it },
            hargaTotal = hargaTotal,
            statusMessage = statusMessage,
            statusType = statusType,
            isProcessing = isProcessing,
            onPesanClick = {
                if (nama.isBlank()) {
                    statusType = StatusType.ERROR
                    statusMessage = "Status : Nama Masih Kosong"
                } else {
                    isProcessing = true
                }
            }
        )
    }
}

@Composable
fun TicketScreenChild(
    modifier: Modifier = Modifier,
    nama: String,
    onNamaChange: (String) -> Unit,
    jumlahTiket: Int,
    onJumlahChange: (Int) -> Unit,
    hargaTotal: Int,
    statusMessage: String,
    statusType: StatusType,
    isProcessing: Boolean,
    onPesanClick: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Input Nama
        Text(text = "Nama", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = nama,
            onValueChange = onNamaChange,
            placeholder = { Text("Masukkan nama Anda") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isProcessing
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Jumlah Tiket", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = { if (jumlahTiket > 1) onJumlahChange(jumlahTiket - 1) },
                enabled = !isProcessing
            ) { Text("-", fontSize = 20.sp) }

            Text(
                text = "$jumlahTiket",
                modifier = Modifier.padding(horizontal = 24.dp),
                fontSize = 18.sp
            )

            OutlinedButton(
                onClick = { onJumlahChange(jumlahTiket + 1) },
                enabled = !isProcessing
            ) { Text("+", fontSize = 20.sp) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Harga Total: Rp $hargaTotal", fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onPesanClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isProcessing,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
        ) {
            Text(text = "Pesan Tiket", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (statusType != StatusType.IDLE) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (statusType) {
                        StatusType.PROCESSING -> Icons.Default.Info
                        StatusType.SUCCESS -> Icons.Default.CheckCircle
                        StatusType.ERROR -> Icons.Default.Warning
                        else -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = when (statusType) {
                        StatusType.PROCESSING -> Color.Gray
                        StatusType.SUCCESS -> Color(0xFF4CAF50) // Hijau
                        StatusType.ERROR -> Color.Red
                        else -> Color.Black
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusMessage,
                    color = when (statusType) {
                        StatusType.PROCESSING -> Color.Gray
                        StatusType.SUCCESS -> Color(0xFF4CAF50)
                        StatusType.ERROR -> Color.Red
                        else -> Color.Black
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

enum class StatusType {
    IDLE, PROCESSING, SUCCESS, ERROR
}