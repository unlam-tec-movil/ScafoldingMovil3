package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.os.Message
import android.util.Log
import androidx.lifecycle.ViewModel
import ar.edu.unlam.mobile.scaffolding.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject



@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    fun saveUser(user: User, onSuccessMessage: (String) -> Unit) {
        db.collection("Users")
            .add(user)
            .addOnSuccessListener {
                onSuccessMessage("Usuario registrado con éxito")
            }
            .addOnFailureListener { e ->
                Log.d(javaClass.simpleName, "${e.message}")
            }
    }
}