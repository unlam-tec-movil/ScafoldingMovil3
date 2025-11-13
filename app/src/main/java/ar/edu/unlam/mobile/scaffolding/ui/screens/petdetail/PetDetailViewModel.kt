package ar.edu.unlam.mobile.scaffolding.ui.screens.petdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.model.Pet
import ar.edu.unlam.mobile.scaffolding.domain.repository.PetsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetDetailViewModel
@Inject
constructor(
    private val repository: PetsRepository,
) : ViewModel() {
    private val _pet = MutableStateFlow<Pet?>(null)
    val pet = _pet.asStateFlow()

    fun loadPet(petId: String) {
        viewModelScope.launch {
            repository.getPetById(petId).collectLatest { pet ->
                _pet.value = pet
            }
        }
    }
}
