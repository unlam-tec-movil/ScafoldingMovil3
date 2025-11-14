package ar.edu.unlam.mobile.scaffolding.data.repository

import ar.edu.unlam.mobile.scaffolding.data.dto.User
import ar.edu.unlam.mobile.scaffolding.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        private val db: FirebaseFirestore,
    ) : UserRepository {
        override suspend fun getCurrentUser(): User? {
            val uid = auth.currentUser?.uid ?: return null
            return getUser(uid)
        }

        override suspend fun getUser(uid: String): User? =
            db
                .collection("Users")
                .document(uid)
                .get()
                .await()
                .toObject(User::class.java)

        override suspend fun saveUser(user: User) {
            db
                .collection("Users")
                .document(user.id)
                .set(user)
                .await()
        }

        override suspend fun createUser(user: User) {
            db
                .collection("Users")
                .document(user.id)
                .set(user)
                .await()
        }

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
