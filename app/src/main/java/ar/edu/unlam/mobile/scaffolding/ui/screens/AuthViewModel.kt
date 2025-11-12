package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.models.User
import ar.edu.unlam.mobile.scaffolding.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        private val userRepository: UserRepository,
    ) : ViewModel() {
        fun registerUser(
            email: String,
            password: String,
            user: User,
            onSuccessMessage: (String) -> Unit,
            onErrorMessage: (String) -> Unit,
        ) {
            auth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->

                    val uid = result.user?.uid
                    if (uid == null) {
                        onErrorMessage("Error obteniendo el UID del usuario")
                        return@addOnSuccessListener
                    }

                    val userWithId = user.copy(id = uid)

                    viewModelScope.launch {
                        try {
                            userRepository.saveUser(userWithId)
                            onSuccessMessage("Usuario registrado con éxito")
                        } catch (e: Exception) {
                            onErrorMessage("Error al guardar datos: ${e.message}")
                        }
                    }
                }.addOnFailureListener {
                    onErrorMessage("Error al registrar: ${it.message}")
                }
        }
    }

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val auth: FirebaseAuth,
    ) : ViewModel() {
        private val _loginResult = MutableStateFlow<Boolean?>(null)
        val loginResult: StateFlow<Boolean?> = _loginResult.asStateFlow()

        fun login(
            email: String,
            password: String,
        ) {
            auth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Log.d("Auth", "User after login: ${user?.uid}")
                        _loginResult.value = true
                    } else {
                        Log.e("Auth", "Login failed: ${task.exception?.message}")
                        _loginResult.value = false
                    }
                }
        }

        fun getCurrentUser(): User? {
            val uid = auth.currentUser?.uid ?: return null
            // Podés usar repository para traer info extra del usuario
            return runBlocking { userRepository.getUser(uid) }
        }
    }
