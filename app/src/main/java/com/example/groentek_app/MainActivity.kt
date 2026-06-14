package com.example.groentek_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.Alignment

data class Plant(
    val id: Int,
    val name: String,
    val soilMoisture: String,
    val favorite: Boolean = false
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PlantListScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen() {

    var plants by remember {
        mutableStateOf(
            listOf(
                Plant(1, "Tomate", "35"),
                Plant(2, "Basilikum", "40")
            )
        )
    }

    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.groentek_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Meine Pflanzen",
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        showDialog = true
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Hinzufügen")
                }
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                items(
                    items = plants,
                    key = { it.id }
                ) { plant ->

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart ||
                                value == SwipeToDismissBoxValue.StartToEnd
                            ) {
                                plants = plants.filter { it.id != plant.id }
                                false
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable {
                                    plants = plants.map {
                                        it.copy(favorite = it.id == plant.id)
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

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Bodenfeuchtigkeit: ${plant.soilMoisture} %",
                                        color = Color.DarkGray
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        plants = plants.map {
                                            it.copy(favorite = it.id == plant.id)
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

        if (showDialog) {

            var plantName by remember { mutableStateOf("") }
            var soilMoisture by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                title = {
                    Text("Neue Pflanze")
                },
                text = {

                    Column {

                        OutlinedTextField(
                            value = plantName,
                            onValueChange = {
                                plantName = it
                            },
                            label = {
                                Text("Name")
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        OutlinedTextField(
                            value = soilMoisture,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) {

                                    val value = it.toIntOrNull()

                                    if (value == null || value in 0..100) {
                                        soilMoisture = it
                                    }
                                }
                            },
                            label = {
                                Text("Bodenfeuchtigkeit %")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
                    }
                },
                confirmButton = {

                    Button(
                        enabled =
                            plantName.isNotBlank() &&
                                    soilMoisture.isNotBlank(),

                        onClick = {

                            val newPlant = Plant(
                                id = plants.size + 1,
                                name = plantName.trim(),
                                soilMoisture = soilMoisture.trim()
                            )

                            plants = plants + newPlant
                            showDialog = false
                        }
                    ) {
                        Text("Speichern")
                    }
                },
                dismissButton = {

                    TextButton(
                        onClick = {
                            showDialog = false
                        }
                    ) {
                        Text("Abbrechen")
                    }
                }
            )
        }
    }
}