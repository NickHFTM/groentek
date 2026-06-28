package com.example.groentek_app.storage

import android.content.Context
import com.example.groentek_app.model.Plant
import org.json.JSONArray
import org.json.JSONObject

/**
 * Handles local persistence for plants.
 *
 * This class stores and loads the plant list using SharedPreferences.
 * SharedPreferences is enough for this first version because the data
 * structure is still small and simple.
 */
object PlantStorage {

    private const val PREFERENCES_NAME = "groentek"
    private const val PLANTS_KEY = "plants"

    /**
     * Saves the complete list of plants locally.
     *
     * The list is converted to JSON and stored as a single string.
     */
    fun savePlants(context: Context, plants: List<Plant>) {
        val jsonArray = JSONArray()

        plants.forEach { plant ->
            val json = JSONObject()

            json.put("id", plant.id)
            json.put("name", plant.name)
            json.put("soilMoisture", plant.soilMoisture)
            json.put("lightHours", plant.lightHours)
            json.put("favorite", plant.favorite)

            jsonArray.put(json)
        }

        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(PLANTS_KEY, jsonArray.toString())
            .apply()
    }

    /**
     * Loads all saved plants from local storage.
     *
     * If no plants are stored yet, an empty list is returned.
     */
    fun loadPlants(context: Context): List<Plant> {
        val jsonString = context
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getString(PLANTS_KEY, null)

        if (jsonString == null) {
            return emptyList()
        }

        val jsonArray = JSONArray(jsonString)
        val plants = mutableListOf<Plant>()

        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)

            plants.add(
                Plant(
                    id = json.getInt("id"),
                    name = json.getString("name"),
                    soilMoisture = json.getString("soilMoisture"),
                    lightHours = json.getString("lightHours"),
                    favorite = json.getBoolean("favorite")
                )
            )
        }

        return plants
    }
}