package com.example.groentek_app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.groentek_app.R
import com.example.groentek_app.dialogs.AddPlantDialog
import com.example.groentek_app.dialogs.SettingsDialog
import com.example.groentek_app.network.Esp32Api
import com.example.groentek_app.storage.PlantStorage
import com.example.groentek_app.storage.SettingsStorage
import kotlinx.coroutines.launch
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState

/**
 * Main screen of the app.
 *
 * This screen is responsible for:
 * - displaying the background image
 * - showing the plant list
 * - selecting one plant
 * - deleting plants by swipe
 * - opening the add-plant dialog
 * - opening the ESP32 settings dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen() {

    // Android context used for local storage.
    val context = LocalContext.current

    // Coroutine scope used for async actions such as testing the ESP32 connection.
    val scope = rememberCoroutineScope()

    // Global ESP32 IP address for the whole app.
    var esp32Ip by remember {
        mutableStateOf(
            SettingsStorage.loadEsp32Ip(context)
        )
    }

    // List of all plants loaded from local storage.
    var plants by remember {
        mutableStateOf(
            PlantStorage.loadPlants(context)
        )
    }

    // Controls whether the add-plant dialog is visible.
    var showAddPlantDialog by remember {
        mutableStateOf(false)
    }

    // Controls whether the ESP32 settings dialog is visible.
    var showSettingsDialog by remember {
        mutableStateOf(false)
    }

    // Text shown after testing the ESP32 connection.
    var connectionStatus by remember {
        mutableStateOf("")
    }

    // Automatically save plants whenever the list changes.
    LaunchedEffect(plants) {
        PlantStorage.savePlants(context, plants)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Fullscreen background image.
        Image(
            painter = painterResource(id = R.drawable.groentek_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Main app layout.
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Meine Pflanzen",
                                color = Color.White
                            )

                            Text(
                                text = "ESP32: $esp32Ip",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                showSettingsDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Einstellungen",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        showAddPlantDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Hinzufügen"
                    )
                }
            }
        ) { padding ->

            // Scrollable list of plants.
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                items(
                    items = plants,
                    key = { plant -> plant.id }
                ) { plant ->

                    // State for swipe-to-delete.
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->

                            if (
                                value == SwipeToDismissBoxValue.EndToStart ||
                                value == SwipeToDismissBoxValue.StartToEnd
                            ) {
                                plants = plants.filter {
                                    it.id != plant.id
                                }

                                // Returning false prevents the item from staying
                                // visually dismissed after deletion.
                                false
                            } else {
                                false
                            }
                        }
                    )

                    // Allows deleting a plant by swiping the card.
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Löschen",
                                    tint = Color.Red
                                )
                            }
                        }
                    ) {

                        // Individual plant card.
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                )
                                .clickable {
                                    // Select this plant and deselect all others.
                                    plants = plants.map {
                                        it.copy(
                                            favorite = it.id == plant.id
                                        )
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.90f)
                            )
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Column {

                                    Text(
                                        text = plant.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.Black
                                    )

                                    Spacer(
                                        modifier = Modifier.height(4.dp)
                                    )

                                    Text(
                                        text = "Bodenfeuchtigkeit: ${plant.soilMoisture} %",
                                        color = Color.DarkGray
                                    )

                                    Text(
                                        text = "Licht: ${plant.lightHours} h",
                                        color = Color.DarkGray
                                    )
                                }

                                // Favorite selector.
                                // Only one plant can be selected at a time.
                                IconButton(
                                    onClick = {
                                        plants = plants.map {
                                            it.copy(
                                                favorite = it.id == plant.id
                                            )
                                        }
                                    }
                                ) {
                                    if (plant.favorite) {
                                        Icon(
                                            imageVector = Icons.Filled.Favorite,
                                            contentDescription = "Ausgewählt",
                                            tint = Color.Red
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Outlined.FavoriteBorder,
                                            contentDescription = "Nicht ausgewählt",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialog for creating a new plant.
        if (showAddPlantDialog) {
            AddPlantDialog(
                existingPlants = plants,
                onDismiss = {
                    showAddPlantDialog = false
                },
                onPlantAdded = { newPlant ->
                    plants = plants + newPlant
                    showAddPlantDialog = false
                }
            )
        }

        // Dialog for configuring and testing the ESP32 connection.
        if (showSettingsDialog) {
            SettingsDialog(
                currentIp = esp32Ip,
                connectionStatus = connectionStatus,
                onDismiss = {
                    showSettingsDialog = false
                },
                onSaveIp = { newIp ->
                    esp32Ip = newIp
                    SettingsStorage.saveEsp32Ip(context, newIp)
                },
                onTestConnection = { ipToTest ->
                    scope.launch {
                        connectionStatus = "Teste Verbindung..."
                        connectionStatus = Esp32Api.testConnection(ipToTest)
                    }
                }
            )
        }
    }
}