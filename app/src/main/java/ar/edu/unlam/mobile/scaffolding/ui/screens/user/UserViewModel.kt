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

data class UserUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
)

@HiltViewModel
class UserViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {
        var uiState by mutableStateOf(UserUiState())
            private set

        fun loadUser(userId: String) {
            uiState = uiState.copy(isLoading = true)
            viewModelScope.launch {
                try {
                    val user = userRepository.getUserById(userId) // tu repo que trae el User desde Firestore
                    uiState = uiState.copy(isLoading = false, user = user)
                } catch (e: Exception) {
                    uiState = uiState.copy(isLoading = false, error = e.message)
                }
            }
        }
    }
