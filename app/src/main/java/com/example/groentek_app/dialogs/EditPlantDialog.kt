package com.example.groentek_app.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.groentek_app.model.Plant

@Composable
fun EditPlantDialog(
    plant: Plant,
    onDismiss: () -> Unit,
    onPlantUpdated: (Plant) -> Unit
) {
    var plantName by remember { mutableStateOf(plant.name) }
    var soilMoisture by remember { mutableStateOf(plant.soilMoisture) }
    var lightHours by remember { mutableStateOf(plant.lightHours) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Pflanze bearbeiten")
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

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = soilMoisture,
                    onValueChange = { newValue ->
                        if (newValue.all { char -> char.isDigit() }) {
                            val value = newValue.toIntOrNull()
                            if (value == null || value in 0..100) {
                                soilMoisture = newValue
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

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lightHours,
                    onValueChange = { newValue ->
                        if (newValue.all { char -> char.isDigit() }) {
                            val value = newValue.toIntOrNull()
                            if (value == null || value in 0..24) {
                                lightHours = newValue
                            }
                        }
                    },
                    label = {
                        Text("Lichtstunden pro Tag")
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
                            soilMoisture.isNotBlank() &&
                            lightHours.isNotBlank(),
                onClick = {
                    onPlantUpdated(
                        plant.copy(
                            name = plantName.trim(),
                            soilMoisture = soilMoisture.trim(),
                            lightHours = lightHours.trim()
                        )
                    )
                }
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Abbrechen")
            }
        }
    )
}