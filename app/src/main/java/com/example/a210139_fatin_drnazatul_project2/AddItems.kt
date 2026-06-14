package com.example.a210139_fatin_drnazatul_project2

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.a210139_fatin_drnazatul_project2.data.FoodEntity
import com.example.a210139_fatin_drnazatul_project2.data.OpenFoodProduct

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemsScreen(navController: NavHostController, viewModel: ResQBiteViewModel) {
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    val myContributions by viewModel.myContributions.collectAsState()
    val foodSearchResults by viewModel.foodSearchResults.collectAsState()
    val isSearchingApi by viewModel.isSearchingApi.collectAsState()
    val apiError by viewModel.apiError.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val isLoadingLocation by viewModel.isLoadingLocation.collectAsState()

    var foodName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var apiSearchQuery by remember { mutableStateOf("") }
    var showApiResults by remember { mutableStateOf(false) }

    val categories = listOf("Bakery", "Canned", "Packed Meals", "Fruits", "Drinks", "Snacks")
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    // autofill location when GPS updates
    LaunchedEffect(currentLocation) {
        if (currentLocation != "Bangi") {
            location = currentLocation
        }
    }

    // permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.fetchCurrentLocation(context)
        }
    }

    fun requestLocation() {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (fineGranted) {
            viewModel.fetchCurrentLocation(context)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 20.dp).fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colorScheme.tertiary)
                    }
                    Text(
                        text = "GIVE AWAY",
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Sharing is Caring!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary)
            Text("Fill in the details of the food you'd like to share.")

            // API seaction
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.tertiaryContainer.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, colorScheme.tertiary)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "🔍 Auto-fill from OpenFoodFacts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colorScheme.tertiary
                    )
                    Text(
                        "Search any food product to auto-fill details!",
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = apiSearchQuery,
                            onValueChange = { apiSearchQuery = it },
                            placeholder = { Text("e.g. croissant, tuna...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = colorScheme.surface,
                                unfocusedContainerColor = colorScheme.surface
                            )
                        )
                        Button(
                            onClick = {
                                viewModel.searchFoodFromApi(apiSearchQuery)
                                showApiResults = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.tertiary)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                    }

                    if (isSearchingApi) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = colorScheme.tertiary)
                        }
                    }

                    apiError?.let { error ->
                        Text(error, color = colorScheme.error, fontSize = 12.sp)
                    }

                    if (showApiResults && foodSearchResults.isNotEmpty()) {
                        Text(
                            "Tap a result to auto-fill:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurfaceVariant
                        )
                        foodSearchResults.forEach { product ->
                            ApiResultItem(
                                product = product,
                                colorScheme = colorScheme,
                                onClick = {
                                    foodName = product.product_name ?: ""
                                    quantity = product.quantity ?: ""
                                    selectedCategory = viewModel.getCategoryFromApiTags(product.categories_tags)
                                    showApiResults = false
                                    viewModel.clearFoodSearch()
                                    apiSearchQuery = ""
                                }
                            )
                        }
                    }

                    if (showApiResults && !isSearchingApi && foodSearchResults.isEmpty() && apiError == null && apiSearchQuery.isNotEmpty()) {
                        Text("No results found. Fill in manually below!", fontSize = 12.sp, color = colorScheme.outline)
                    }
                }
            }

            // manual form section
            HorizontalDivider()
            Text("Or fill in manually:", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            // name
            OutlinedTextField(
                value = foodName,
                onValueChange = { foodName = it },
                label = { Text("Food Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // category
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = { selectedCategory = category; expanded = false }
                        )
                    }
                }
            }

            // quantity/portion
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity / Portion") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // GPS location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Pick-up Location") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Button(
                    onClick = { requestLocation() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                ) {
                    if (isLoadingLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.MyLocation, contentDescription = "Use GPS")
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (foodName.isNotEmpty()) {
                        val finalLocation = if (location.isBlank()) "Bangi" else location
                        val categoryIcon = viewModel.getIconForCategory(selectedCategory)
                        viewModel.addMyFoodItem(
                            FoodEntity(
                                title = foodName,
                                distance = "0.0km",
                                user = "Me",
                                location = finalLocation,
                                imageRes = categoryIcon,
                                isUserContribution = true
                            )
                        )
                        foodName = ""; quantity = ""; location = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.tertiary)
            ) {
                Text("List My Food!", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Your Contribution Gallery", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (myContributions.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No items shared yet.", color = colorScheme.outline)
                }
            } else {
                myContributions.reversed().forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                        border = BorderStroke(1.dp, colorScheme.outlineVariant)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).background(colorScheme.primary, CircleShape))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(item.title, fontWeight = FontWeight.Bold)
                                Text("Available at ${item.location}", fontSize = 12.sp, color = colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApiResultItem(product: OpenFoodProduct, colorScheme: ColorScheme, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colorScheme.tertiaryContainer, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🍱", fontSize = 20.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.product_name ?: "Unknown",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                if (!product.brands.isNullOrBlank()) {
                    Text(text = product.brands, fontSize = 12.sp, color = colorScheme.onSurfaceVariant, maxLines = 1)
                }
                if (!product.quantity.isNullOrBlank()) {
                    Text(text = product.quantity, fontSize = 11.sp, color = colorScheme.outline)
                }
            }
            Text("Tap to use", fontSize = 10.sp, color = colorScheme.tertiary)
        }
    }
}