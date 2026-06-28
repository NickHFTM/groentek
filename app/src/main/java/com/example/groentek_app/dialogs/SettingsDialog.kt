package com.example.groentek_app.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Dialog for configuring the global ESP32 connection.
 *
 * The app uses one ESP32 IP address for the whole system.
 * This dialog allows the user to:
 * - edit the ESP32 IP address
 * - save the IP address
 * - test the connection
 */
@Composable
fun SettingsDialog(
    currentIp: String,
    connectionStatus: String,
    onDismiss: () -> Unit,
    onSaveIp: (String) -> Unit,
    onTestConnection: (String) -> Unit
) {
    // Temporary input value.
    // The real app setting is only updated when the user saves or confirms.
    var tempIp by remember {
        mutableStateOf(currentIp)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("ESP32 Einstellungen")
        },
        text = {
            Column {

                // Input field for the global ESP32 IP address.
                OutlinedTextField(
                    value = tempIp,
                    onValueChange = {
                        tempIp = it
                    },
                    label = {
                        Text("ESP32 IP-Adresse")
                    },
                    placeholder = {
                        Text("192.168.1.50")
                    }
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // Saves the IP address without closing the dialog.
                Button(
                    onClick = {
                        onSaveIp(tempIp.trim())
                    }
                ) {
                    Text("Speichern")
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // Tests whether the ESP32 is reachable.
                Button(
                    onClick = {
                        onTestConnection(tempIp.trim())
                    }
                ) {
                    Text("Verbindung testen")
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // Shows the latest connection test result.
                Text(connectionStatus)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSaveIp(tempIp.trim())
                    onDismiss()
                }
            ) {
                Text("OK")
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