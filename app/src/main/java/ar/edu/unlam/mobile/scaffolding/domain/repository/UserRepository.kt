package ar.edu.unlam.mobile.scaffolding.domain.repository

import ar.edu.unlam.mobile.scaffolding.domain.model.User

interface UserRepository {
    suspend fun getCurrentUser(): User?

    suspend fun getUserById(userId: String): User?

    suspend fun saveUser(user: User)

    suspend fun createUser(user: User)

    suspend fun addPostToUser(
        userId: String,
        postId: String,
    )
}
