package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
sealed interface TextListUIState {
    data class Success(
        val list: MutableList<String>,
    ) : TextListUIState

    data object Loading : TextListUIState

    data class Error(
        val message: String,
    ) : TextListUIState
}

@Immutable
sealed interface UserNameUIState {
    data class Success(
        val name: String,
    ) : UserNameUIState

    data object Loading : UserNameUIState

    data class Error(
        val message: String,
    ) : UserNameUIState
}

data class UserUIState(
    val textListState: TextListUIState,
    val userNameState: UserNameUIState,
)

@HiltViewModel
class UserViewModel
    @Inject
    constructor() : ViewModel() {
        private val nombre = MutableStateFlow("")

        private val mutableExampleList =
            MutableStateFlow<List<String>>(
                mutableListOf(
                    "Hola",
                    "Soy",
                    "Una",
                    "Lista",
                    "De",
                    "Ejemplo",
                ),
            )

        private val _uiState =
            MutableStateFlow(
                UserUIState(
                    textListState = TextListUIState.Loading,
                    userNameState = UserNameUIState.Loading,
                ),
            )
        val uiState = _uiState.asStateFlow()

        init {
            viewModelScope.launch {
                delay(2000)
                _uiState.value =
                    _uiState.value.copy(
                        userNameState = UserNameUIState.Success("2b"),
                    )
                delay(2000)
                _uiState.value =
                    _uiState.value.copy(
                        textListState = TextListUIState.Error("se rompio todo"),
                    )
            }
        }
    }
