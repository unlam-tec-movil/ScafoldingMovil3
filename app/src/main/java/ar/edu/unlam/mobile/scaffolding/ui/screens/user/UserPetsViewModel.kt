package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.model.Pet
import ar.edu.unlam.mobile.scaffolding.domain.repository.PetsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserPetsUiState(
    val isLoading: Boolean = false,
    val pets: List<Pet> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class UserPetsViewModel
    @Inject
    constructor(
        private val petsRepository: PetsRepository,
    ) : ViewModel() {
        var uiState by mutableStateOf(UserPetsUiState())
            private set

        fun loadUserPets(ids: List<String>) {
            uiState = uiState.copy(isLoading = true)
            viewModelScope.launch {
                try {
                    petsRepository.getPetsByIds(ids).collectLatest { pets ->
                        uiState = uiState.copy(isLoading = false, pets = pets)
                    }
                } catch (e: Exception) {
                    uiState = uiState.copy(isLoading = false, error = e.message)
                }
            }
        }
    }
