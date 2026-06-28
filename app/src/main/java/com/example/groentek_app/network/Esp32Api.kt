package com.example.groentek_app.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Handles all communication with the ESP32.
 *
 * All HTTP requests to the controller should be implemented here.
 * This keeps the UI separated from networking.
 */
object Esp32Api {

    /**
     * Tests whether the ESP32 is reachable.
     *
     * Endpoint:
     * GET /status
     */
    suspend fun testConnection(ip: String): String {

        return withContext(Dispatchers.IO) {

            try {

                val connection = URL("http://$ip/status")
                    .openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 3000
                connection.readTimeout = 3000

                when (connection.responseCode) {
                    200 -> "Connection successful"
                    else -> "HTTP ${connection.responseCode}"
                }

            } catch (e: Exception) {
                "Connection failed: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Sends the selected plant configuration to the ESP32.
     *
     * Endpoint:
     * POST /settings
     */
    suspend fun sendPlantConfiguration(
        ip: String,
        soilMoisture: Int,
        lightHours: Int
    ): Boolean {

        return withContext(Dispatchers.IO) {

            try {

                val connection = URL("http://$ip/settings")
                    .openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                val json = JSONObject().apply {
                    put("soilMoisture", soilMoisture)
                    put("lightHours", lightHours)
                }

                OutputStreamWriter(connection.outputStream).use {
                    it.write(json.toString())
                    it.flush()
                }

                connection.responseCode == 200

            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Requests the current sensor values from the ESP32.
     *
     * Endpoint:
     * GET /status
     *
     * Example response:
     *
     * {
     *   "soilMoisture":42,
     *   "pump":false,
     *   "light":true
     * }
     */
    suspend fun getStatus(ip: String): JSONObject? {

        return withContext(Dispatchers.IO) {

            try {

                val connection = URL("http://$ip/status")
                    .openConnection() as HttpURLConnection

                connection.requestMethod = "GET"

                val response = connection.inputStream
                    .bufferedReader()
                    .readText()

                JSONObject(response)

            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Starts the water pump manually.
     *
     * Endpoint:
     * POST /pump
     */
    suspend fun startPump(ip: String): Boolean {

        return withContext(Dispatchers.IO) {

            try {

                val connection = URL("http://$ip/pump")
                    .openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doOutput = true

                connection.responseCode == 200

            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Turns the grow light on or off.
     *
     * Endpoint:
     * POST /light
     */
    suspend fun setLight(
        ip: String,
        enabled: Boolean
    ): Boolean {

        return withContext(Dispatchers.IO) {

            try {

                val connection = URL("http://$ip/light")
                    .openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                val json = JSONObject().apply {
                    put("enabled", enabled)
                }

                OutputStreamWriter(connection.outputStream).use {
                    it.write(json.toString())
                    it.flush()
                }

                connection.responseCode == 200

            } catch (e: Exception) {
                false
            }
        }
    }
}