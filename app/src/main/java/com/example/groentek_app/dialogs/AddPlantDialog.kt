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

/**
 * Dialog for creating a new plant.
 *
 * This dialog collects all values that are needed for the plant:
 * - name
 * - minimum soil moisture
 * - daily light hours
 *
 * Validation is handled directly inside the input fields:
 * - soil moisture must be a number between 0 and 100
 * - light hours must be a number between 0 and 24
 */
@Composable
fun AddPlantDialog(
    existingPlants: List<Plant>,
    onDismiss: () -> Unit,
    onPlantAdded: (Plant) -> Unit
) {
    // Local dialog state for all input fields.
    var plantName by remember { mutableStateOf("") }
    var soilMoisture by remember { mutableStateOf("") }
    var lightHours by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Neue Pflanze")
        },
        text = {
            Column {

                // Free text input for the plant name.
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

                // Numeric input for the desired minimum soil moisture.
                // Only numbers between 0 and 100 are accepted.
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

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                // Numeric input for the daily light duration.
                // Only numbers between 0 and 24 are accepted.
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

                    // Generate a stable unique ID.
                    // This avoids duplicate IDs after plants have been deleted.
                    val newPlantId =
                        (existingPlants.maxOfOrNull { it.id } ?: 0) + 1

                    val newPlant = Plant(
                        id = newPlantId,
                        name = plantName.trim(),
                        soilMoisture = soilMoisture.trim(),
                        lightHours = lightHours.trim()
                    )

                    onPlantAdded(newPlant)
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