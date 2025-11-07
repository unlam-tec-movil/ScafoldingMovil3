package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.lifecycle.ViewModel
import ar.edu.unlam.mobile.scaffolding.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
    @Inject
    constructor(
        private val db: FirebaseFirestore,
        private val auth: FirebaseAuth,
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
                    if (uid != null) {
                        val userData = user.copy(id = uid)

                        db
                            .collection("Users")
                            .document(uid)
                            .set(userData)
                            .addOnSuccessListener {
                                onSuccessMessage("Usuario registrado con éxito")
                            }.addOnFailureListener { e ->
                                onErrorMessage("Error al guardar datos: ${e.message}")
                            }
                    } else {
                        onErrorMessage("No se pudo obtener el ID del usuario")
                    }
                }.addOnFailureListener { e ->
                    onErrorMessage("Error al registrar: ${e.message}")
                }
        }
    }
