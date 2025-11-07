package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ar.edu.unlam.mobile.scaffolding.data.models.PlacePin
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

// Extensión de DataStore para Context
private val Context.pinsDataStore by preferencesDataStore("map_pins_ds")
private val PINS_JSON_KEY = stringPreferencesKey("pins_json")

/**
 * DataSource que maneja la persistencia local de pins usando DataStore.
 *
 * Esta clase encapsula TODA la lógica de acceso a datos local:
 * - Configuración de DataStore
 * - Serialización/Deserialización JSON
 * - Operaciones de lectura/escritura
 */
class PinLocalDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        /**
         * Obtiene todos los pins guardados en DataStore.
         */
        suspend fun getAllPins(): List<PlacePin> =
            try {
                val prefs = context.pinsDataStore.data.first()
                val jsonString = prefs[PINS_JSON_KEY] ?: "[]"
                Json.decodeFromString<List<PlacePin>>(jsonString)
            } catch (e: Exception) {
                emptyList()
            }

        /**
         * Guarda una lista de pins en DataStore.
         */
        suspend fun savePins(pins: List<PlacePin>): Boolean =
            try {
                val jsonString = Json.encodeToString(pins)
                context.pinsDataStore.edit { preferences ->
                    preferences[PINS_JSON_KEY] = jsonString
                }
                true
            } catch (e: Exception) {
                false
            }
    }
