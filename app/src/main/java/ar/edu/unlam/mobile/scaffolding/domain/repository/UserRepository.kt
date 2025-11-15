package ar.edu.unlam.mobile.scaffolding.domain.repository

import ar.edu.unlam.mobile.scaffolding.domain.model.User

/**
 * Puerto de salida.
 * Define el contrato para operaciones relacionadas con usuarios.
 * La implementación (UserRepositoryImpl en la capa de datos) es responsable de:
 * - Obtener datos de Firebase (UserDto)
 * - Mapear DTO → Domain antes de devolver
 * - Mapear Domain → DTO antes de guardar
 */
interface UserRepository {
    /**
     * Obtiene el usuario actual autenticado.
     */
    suspend fun getCurrentUser(): User?

    /**
     * Obtiene un usuario por su ID.
     */
    suspend fun getUser(uid: String): User?

    /**
     * Guarda o actualiza un usuario.
     */
    suspend fun saveUser(user: User)

    /**
     * Crea un nuevo usuario.
     */
    suspend fun createUser(user: User)

    /**
     * Agrega un post (mascota) a la lista de posts del usuario.
     */
    suspend fun addPostToUser(
        userId: String,
        postId: String,
    )
}
