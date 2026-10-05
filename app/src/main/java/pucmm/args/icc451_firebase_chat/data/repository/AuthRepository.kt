package pucmm.args.icc451_firebase_chat.data.repository

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class AuthRepository {

	val currentUserId: String = "1"

	fun isUserLoggedIn(): Boolean = true

	suspend fun signIn(email: String, password: String): Boolean {
		delay(200.milliseconds)
		return email == "args0001@example.com" && password == "password123"
	}

	suspend fun signOut() {
		delay(200.milliseconds)
	}

	suspend fun register(email: String, password: String): Boolean {
		delay(200.milliseconds)
		return email.isNotEmpty() && password.isNotEmpty()
	}
}