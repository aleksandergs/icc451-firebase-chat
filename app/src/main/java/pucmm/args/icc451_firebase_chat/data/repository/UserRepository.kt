package pucmm.args.icc451_firebase_chat.data.repository

import kotlinx.coroutines.delay
import pucmm.args.icc451_firebase_chat.data.model.User
import kotlin.time.Duration.Companion.milliseconds

class UserRepository {

	suspend fun saveUser(user: User) {
		delay(200.milliseconds)
	}

	suspend fun getUser(id: Long): User? {
		delay(200.milliseconds)
		return User(id, "John Doe #$id", "john.doe$id@example.com")
	}

	suspend fun getAllUsers(): List<User> {
		delay(200.milliseconds)
		val users = mutableListOf<User>()
		for (i in 1L..10L)
			users.add(getUser(i)!!)
		return users
	}
}