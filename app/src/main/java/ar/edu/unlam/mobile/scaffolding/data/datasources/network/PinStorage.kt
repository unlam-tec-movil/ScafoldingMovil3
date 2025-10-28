package ar.edu.unlam.mobile.scaffolding.data.datasources.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ar.edu.unlam.mobile.scaffolding.data.models.PlacePin

private val Context.ds by preferencesDataStore("map_pins_ds")
private val PINS_JSON = stringPreferencesKey("pins_json")

object PinStorage {
    suspend fun load(context: Context): List<PlacePin> {
        val prefs = context.ds.data.first()
        val raw = prefs[PINS_JSON] ?: "[]"
        return runCatching { Json.decodeFromString<List<PlacePin>>(raw) }.getOrElse { emptyList() }
    }

    suspend fun save(context: Context, pins: List<PlacePin>) {
        val json = Json.encodeToString(pins)
        context.ds.edit { it[PINS_JSON] = json }
    }
}
