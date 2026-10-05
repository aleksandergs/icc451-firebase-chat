package pucmm.args.icc451_firebase_chat.data.repository

import androidx.annotation.StringRes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await
import pucmm.args.icc451_firebase_chat.R

// Resultado de una operacion de autenticacion
sealed interface AuthResult {

	data object Success : AuthResult

	data class Failure(@StringRes val messageRes: Int) : AuthResult
}

class AuthRepository(
	private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {

	val currentUserId: String get() = auth.currentUser?.uid.orEmpty()

	// Firebase guarda la sesion en el dispositivo, así que al reabrir la app el usuario sigue autenticado a no ser que haya cerrado sesion
	fun isUserLoggedIn(): Boolean = auth.currentUser != null

	suspend fun signIn(email: String, password: String): AuthResult = try {
		auth.signInWithEmailAndPassword(email, password).await()
		AuthResult.Success
	} catch (e: FirebaseAuthException) {
		AuthResult.Failure(signInErrorRes(e))
	} catch (e: Exception) {
		AuthResult.Failure(R.string.network_error)
	}

	suspend fun register(email: String, password: String): AuthResult = try {
		auth.createUserWithEmailAndPassword(email, password).await()
		AuthResult.Success
	} catch (e: FirebaseAuthException) {
		AuthResult.Failure(registerErrorRes(e))
	} catch (e: Exception) {
		AuthResult.Failure(R.string.network_error)
	}

	fun signOut() {
		auth.signOut()
	}

	private fun signInErrorRes(e: FirebaseAuthException): Int = when (e.errorCode) {
		ERROR_INVALID_CREDENTIAL,
		ERROR_WRONG_PASSWORD,
		ERROR_USER_NOT_FOUND,
		-> R.string.invalid_credentials_error
		ERROR_INVALID_EMAIL -> R.string.email_invalid_error
		ERROR_NETWORK_REQUEST_FAILED -> R.string.network_error
		else -> R.string.unknown_error
	}

	private fun registerErrorRes(e: FirebaseAuthException): Int = when (e.errorCode) {
		ERROR_EMAIL_ALREADY_IN_USE -> R.string.email_in_use_error
		ERROR_WEAK_PASSWORD -> R.string.weak_password_error
		ERROR_INVALID_EMAIL -> R.string.email_invalid_error
		ERROR_NETWORK_REQUEST_FAILED -> R.string.network_error
		else -> R.string.unknown_error
	}

	// Strings Codigos de error de Android
	private companion object {
		const val ERROR_INVALID_CREDENTIAL = "ERROR_INVALID_CREDENTIAL"
		const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
		const val ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
		const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"
		const val ERROR_NETWORK_REQUEST_FAILED = "ERROR_NETWORK_REQUEST_FAILED"
		const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
		const val ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"
	}
}