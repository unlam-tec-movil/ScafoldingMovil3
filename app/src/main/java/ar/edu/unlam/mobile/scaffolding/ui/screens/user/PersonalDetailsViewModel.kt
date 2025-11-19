package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.model.User
import ar.edu.unlam.mobile.scaffolding.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PersonalDetailsUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: String? = null,
)

@HiltViewModel
class PersonalDetailsViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {
        var uiState by mutableStateOf(PersonalDetailsUiState())
            private set

        init {
            loadUser()
        }

        private fun loadUser() {
            viewModelScope.launch {
                uiState = uiState.copy(isLoading = true)
                try {
                    val user = userRepository.getCurrentUser()
                    uiState = uiState.copy(isLoading = false, user = user)
                } catch (e: Exception) {
                    uiState = uiState.copy(isLoading = false, error = e.message)
                }
            }
        }
    }
