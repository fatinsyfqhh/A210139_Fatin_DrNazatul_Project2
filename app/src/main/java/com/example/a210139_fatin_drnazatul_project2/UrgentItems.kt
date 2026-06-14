package com.example.a210139_fatin_drnazatul_project2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrgentItemsScreen(navController: NavHostController) {
    val colorScheme = MaterialTheme.colorScheme
    var showUrgencyDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("") }
    var selectedStore by remember { mutableStateOf("") }

    if (showUrgencyDialog) {
        AlertDialog(
            onDismissRequest = { showUrgencyDialog = false },
            icon = { Icon(Icons.Default.Timer, contentDescription = null, tint = Color.Red) },
            title = {
                Text(text = "Emergency Rescue!", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            },
            text = {
                Text(
                    text = "The $selectedItem at $selectedStore will be thrown away very soon. Are you sure you can rescue it in time?",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUrgencyDialog = false
                        showSuccessDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                ) {
                    Text("Yes, I'm on my way!")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUrgencyDialog = false }) {
                    Text("Not this time", color = colorScheme.outline)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(text = "Item Reserved!", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Awesome! We've notified the provider at $selectedStore. Please arrive within 1 hour to collect your $selectedItem.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Got it!")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            Surface(
                color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 20.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) { Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = colorScheme.tertiary
                        ) }
                    Text(
                        text = "⏰ EXPIRING SOON",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.tertiary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // header info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer)
            ) {
                Text(
                    text = "Rescue these items within Bangi before they're gone!",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Nearby Rescues",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // list of items
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    UrgentItemBox(
                        storeName = "EMart24 UKM",
                        itemName = "Egg Mayo Sandwiches (3x)",
                        timeLeft = "45 mins left",
                        colorScheme = colorScheme,
                        onClick = {
                            selectedItem = "Egg Mayo Sandwiches (3x)"
                            selectedStore = "EMart24 UKM"
                            showUrgencyDialog = true
                        }
                    )
                }
                item {
                    UrgentItemBox(
                        storeName = "DECTAR",
                        itemName = "Nasi Ayam Masak Merah (5x)",
                        timeLeft = "1 hour left",
                        colorScheme = colorScheme,
                        onClick = {
                            selectedItem = "Nasi Ayam Masak Merah (5x)"
                            selectedStore = "DECTAR"
                            showUrgencyDialog = true
                        }
                    )
                }
                item {
                    UrgentItemBox(
                        storeName = "Family Mart",
                        itemName = "Onigiri Tuna (2x)",
                        timeLeft = "15 mins left",
                        colorScheme = colorScheme,
                        onClick = {
                            selectedItem = "Onigiri Tuna (2x)"
                            selectedStore = "Family Mart"
                            showUrgencyDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UrgentItemBox(storeName: String, itemName: String, timeLeft: String, colorScheme: ColorScheme, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, colorScheme.tertiary),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = storeName,
                    fontSize = 12.sp,
                    color = colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = itemName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // urgency badge
            Surface(
                color = colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Timer,
                        null,
                        modifier = Modifier.size(14.dp),
                        tint = colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = timeLeft,
                        color = colorScheme.onErrorContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}