package ar.edu.unlam.mobile.scaffolding.ui.screens.map

import android.content.Context
import androidx.lifecycle.ViewModel
import ar.edu.unlam.mobile.scaffolding.data.models.PlacePin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val _pins = MutableStateFlow<List<PlacePin>>(emptyList())
    val pins: StateFlow<List<PlacePin>> = _pins

    fun setAll(pins: List<PlacePin>?) {
        if (pins != null) {
            _pins.value = pins
        }
    }

    fun load(context: Context) : List<PlacePin>? {
        return null
    }

    // estado para “nuevo pin” al hacer long press
    private val _pendingLatLng = MutableStateFlow<com.google.android.gms.maps.model.LatLng?>(null)

    val pendingLatLng: StateFlow<com.google.android.gms.maps.model.LatLng?> = _pendingLatLng

    fun askAddAt(latLng: com.google.android.gms.maps.model.LatLng) {
        _pendingLatLng.value = latLng
    }

    fun confirmAdd(title: String, snippet: String?) {
        val pos = _pendingLatLng.value ?: return
        _pins.value = _pins.value + PlacePin(
            lat = pos.latitude, lng = pos.longitude,
            title = title, snippet = snippet
        )
        _pendingLatLng.value = null
    }

    fun cancelAdd() { _pendingLatLng.value = null }

    fun removePin(id: String) { _pins.value = _pins.value.filterNot { it.id == id } }

    fun movePin(id: String, newPos: com.google.android.gms.maps.model.LatLng) {
        _pins.value = _pins.value.map {
            if (it.id == id) it.copy(lat = newPos.latitude, lng = newPos.longitude) else it
        }
    }
}