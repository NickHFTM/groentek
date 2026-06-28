package com.example.groentek_app.storage

import android.content.Context

/**
 * Handles application settings.
 *
 * Currently this class only stores the ESP32 IP address.
 * More settings (theme, language, notifications, etc.)
 * can easily be added later.
 */
object SettingsStorage {

    private const val PREFERENCES_NAME = "groentek"

    // SharedPreferences key
    private const val ESP32_IP_KEY = "esp32_ip"

    // Default IP shown when the user has never configured one.
    private const val DEFAULT_ESP32_IP = "192.168.1.50"

    /**
     * Saves the ESP32 IP address.
     */
    fun saveEsp32Ip(
        context: Context,
        ip: String
    ) {
        context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putString(ESP32_IP_KEY, ip)
            .apply()
    }

    /**
     * Loads the stored ESP32 IP address.
     *
     * Returns the default IP if none has been saved yet.
     */
    fun loadEsp32Ip(
        context: Context
    ): String {

        return context
            .getSharedPreferences(
                PREFERENCES_NAME,
                Context.MODE_PRIVATE
            )
            .getString(
                ESP32_IP_KEY,
                DEFAULT_ESP32_IP
            ) ?: DEFAULT_ESP32_IP
    }

    /**
     * Removes the saved ESP32 IP address.
     *
     * After calling this function the application
     * will use the default IP again.
     */
    fun clearEsp32Ip(
        context: Context
    ) {
        context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .remove(ESP32_IP_KEY)
            .apply()
    }
}