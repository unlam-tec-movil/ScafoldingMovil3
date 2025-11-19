package ar.edu.unlam.mobile.scaffolding.data.repository

import ar.edu.unlam.mobile.scaffolding.data.dto.UserDto
import ar.edu.unlam.mobile.scaffolding.data.mappers.toDomain
import ar.edu.unlam.mobile.scaffolding.data.mappers.toDto
import ar.edu.unlam.mobile.scaffolding.domain.model.User
import ar.edu.unlam.mobile.scaffolding.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementación de UserRepository que conecta con Firebase Firestore.
 * - Convierte entre UserDto (Firebase) y User (dominio) usando mappers.
 * - Provee operaciones CRUD básicas sobre la colección "Users".
 */
class UserRepositoryImpl
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        private val db: FirebaseFirestore,
    ) : UserRepository {
        /**
         * Obtiene el usuario actualmente autenticado en FirebaseAuth.
         */
        override suspend fun getCurrentUser(): User? {
            val uid = auth.currentUser?.uid ?: return null
            return getUserById(uid)
        }

        /**
         * Obtiene un usuario por su ID desde Firestore.
         */
        override suspend fun getUserById(userId: String): User? {
            val snapshot =
                db
                    .collection("Users")
                    .document(userId)
                    .get()
                    .await()
            val dto = snapshot.toObject(UserDto::class.java)
            return dto?.copy(id = snapshot.id)?.toDomain()
        }

        /**
         * Guarda o actualiza un usuario en Firestore.
         */
        override suspend fun saveUser(user: User) {
            val dto = user.toDto()
            db
                .collection("Users")
                .document(dto.id)
                .set(dto)
                .await()
        }

        /**
         * Crea un nuevo usuario en Firestore.
         */
        override suspend fun createUser(user: User) {
            val dto = user.toDto()
            db
                .collection("Users")
                .document(dto.id)
                .set(dto)
                .await()
        }

        /**
         * Agrega un post (mascota) a la lista de posts del usuario.
         * Usa FieldValue.arrayUnion para evitar duplicados.
         */
        override suspend fun addPostToUser(
            userId: String,
            postId: String,
        ) {
            db
                .collection("Users")
                .document(userId)
                .update("posts", FieldValue.arrayUnion(postId))
                .await()
        }
    }
