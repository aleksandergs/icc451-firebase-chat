package pucmm.args.icc451_firebase_chat.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pucmm.args.icc451_firebase_chat.data.model.User

class UserRepository(
	private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

	// users/{uid}
	private val usersCollection get() = firestore.collection("users")

	suspend fun saveUser(user: User) {
		usersCollection.document(user.id).set(user.toMap()).await()
	}

	suspend fun getUser(id: String): User? =
		usersCollection.document(id).get().await().toUser()

	suspend fun getAllUsers(): List<User> =
		usersCollection.get().await().documents.mapNotNull { it.toUser() }
}