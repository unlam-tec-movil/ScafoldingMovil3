package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.widget.Spinner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.PinnableContainer
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.ui.components.Greeting
import ar.edu.unlam.mobile.scaffolding.ui.components.TextList
import kotlinx.coroutines.launch

@Composable
fun UserScreen(
    userId: String,
    onError: (Exception) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    Column {
        Box(
            modifier =
                Modifier
                    .padding(16.dp),
        ) {
            when (val userState = uiState.value.userNameState) {
                is UserNameUIState.Loading -> {
                    CircularProgressIndicator()
                }

                is UserNameUIState.Success -> {
                    Column {
                        Greeting(userState.name, modifier)
                    }
                }

                is UserNameUIState.Error -> {
                    // Error
                }
            }
        }
        Box(
            modifier =
                Modifier
                    .padding(16.dp),
        ) {
            when (val textListState = uiState.value.textListState) {
                is TextListUIState.Loading -> {
                    CircularProgressIndicator()
                }

                is TextListUIState.Success -> {
                    TextList(textListState.list)
                }

                is TextListUIState.Error -> {
                    onError(Exception(textListState.message))
                }
            }
        }
    }
}
