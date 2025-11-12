package ar.edu.unlam.mobile.scaffolding.domain.repository

import ar.edu.unlam.mobile.scaffolding.data.dto.User

interface UserRepository {
    suspend fun getCurrentUser(): User?

    suspend fun getUser(uid: String): User?

    suspend fun saveUser(user: User)

    suspend fun createUser(user: User)

    suspend fun addPostToUser(
        userId: String,
        postId: String,
    )
}
