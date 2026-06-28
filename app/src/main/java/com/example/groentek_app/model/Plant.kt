package com.example.groentek_app.model

/**
 * Data model representing a single plant in the app.
 *
 * Each plant contains the configuration values that are relevant
 * for watering and lighting.
 */
data class Plant(
    /**
     * Unique identifier for the plant.
     * This is used by the list to keep items stable.
     */
    val id: Int,

    /**
     * Display name of the plant.
     * Example: "Tomate", "Basilikum"
     */
    val name: String,

    /**
     * Desired minimum soil moisture in percent.
     * Example: "35" means 35%.
     */
    val soilMoisture: String,

    /**
     * Desired number of light hours per day.
     * Valid range should be 0 to 24.
     */
    val lightHours: String,

    /**
     * Marks whether this plant is currently selected.
     * Only one plant should be selected at a time.
     */
    val favorite: Boolean = false
)