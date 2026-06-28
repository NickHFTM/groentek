package com.example.groentek_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.groentek_app.screens.PlantListScreen
import com.example.groentek_app.ui.theme.GroentekappTheme

/**
 * Main entry point of the Gröntek application.
 *
 * This activity starts the Compose user interface.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GroentekappTheme {
                PlantListScreen()
            }
        }
    }
}