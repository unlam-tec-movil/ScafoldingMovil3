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
 * Implementación de UserRepository que conecta con Firebase.
 * - Obtener datos de Firebase como UserDto (capa de datos)
 * - Mapear DTO → Domain antes de devolver (usando UserMapper)
 * - Mapear Domain → DTO antes de guardar (usando UserMapper)
 */
class UserRepositoryImpl
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        private val db: FirebaseFirestore,
    ) : UserRepository {
        /**
         * Obtiene el usuario actual autenticado.
         */
        override suspend fun getCurrentUser(): User? {
            val uid = auth.currentUser?.uid ?: return null
            return getUser(uid)
        }

        /**
         * Obtiene un usuario por su ID desde Firebase.
         *
         * Flujo:
         * 1. Obtiene el documento de Firebase (UserDto)
         * 2. Mapea DTO → Domain usando UserMapper.toDomain()
         * 3. Devuelve User del dominio
         */
        override suspend fun getUser(uid: String): User? {
            // 1. Obtener DTO de Firebase
            val userDto =
                db
                    .collection("Users")
                    .document(uid)
                    .get()
                    .await()
                    .toObject(UserDto::class.java)

            // 2. Mapear DTO → Domain (o null si no existe)
            return userDto?.toDomain()
        }

        /**
         * Guarda o actualiza un usuario en Firebase.
         *
         * Flujo:
         * 1. Mapea Domain → DTO usando UserMapper.toDto()
         * 2. Guarda el DTO en Firebase
         */
        override suspend fun saveUser(user: User) {
            // 1. Mapear Domain → DTO
            val userDto = user.toDto()

            // 2. Guardar en Firebase
            db
                .collection("Users")
                .document(userDto.id)
                .set(userDto)
                .await()
        }

        /**
         * Crea un nuevo usuario en Firebase.
         *
         * Flujo:
         * 1. Mapea Domain → DTO usando UserMapper.toDto()
         * 2. Crea el DTO en Firebase
         */
        override suspend fun createUser(user: User) {
            // 1. Mapear Domain → DTO
            val userDto = user.toDto()

            // 2. Crear en Firebase
            db
                .collection("Users")
                .document(userDto.id)
                .set(userDto)
                .await()
        }

        /**
         * Agrega un post (mascota) a la lista de posts del usuario.
         *
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
