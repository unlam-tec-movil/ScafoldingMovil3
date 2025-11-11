package ar.edu.unlam.mobile.scaffolding.ui.screens.userPosts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.models.Pet
import ar.edu.unlam.mobile.scaffolding.data.models.Status
import ar.edu.unlam.mobile.scaffolding.domain.repository.PetsRepository
import ar.edu.unlam.mobile.scaffolding.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MyPetsViewModel
    @Inject
    constructor(
        private val petsRepository: PetsRepository,
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private val _posts = MutableStateFlow<List<Pet>>(emptyList())
        val posts = _posts.asStateFlow()

        val lostPosts: StateFlow<List<Pet>> =
            posts
                .map { list ->
                    list.filter { it.status == Status.LOST }
                }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        val foundPosts: StateFlow<List<Pet>> =
            posts
                .map { list ->
                    list.filter { it.status == Status.FOUND }
                }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        init {
            viewModelScope.launch {
                val user = userRepository.getCurrentUser()
                Log.d("MyPetsVM", "Usuario: $user")

                if (user != null && user.posts.isNotEmpty()) {
                    petsRepository.getPetsByIds(user.posts).collect { list ->
                        _posts.value = list
                    }
                }
            }
        }
    }
