package pucmm.args.icc451_firebase_chat.ui.auth

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.Chat451App
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.data.repository.AuthRepository
import pucmm.args.icc451_firebase_chat.ui.base.BaseViewModel
import pucmm.args.icc451_firebase_chat.utils.ValidationUtils

data class LoginUiState(
	val email: String = "",
	val password: String = "",
	val emailError: Int? = null,
	val passwordError: Int? = null,
	val credentialsError: Int? = null,
	val isLoading: Boolean = false,
	val isLoggedIn: Boolean = false,
)

class LoginViewModel(
	private val authRepository: AuthRepository = Chat451App.authRepository,
) : BaseViewModel<LoginUiState>(LoginUiState()) {

	fun onEmailChange(email: String) {
		updateState { it.copy(email = email, emailError = null, credentialsError = null) }
	}

	fun onPasswordChange(password: String) {
		updateState { it.copy(password = password, passwordError = null, credentialsError = null) }
	}

	fun login() {
		val loginState = currentState
		val emailError = ValidationUtils.validateEmail(loginState.email)
		val passwordError = ValidationUtils.validatePassword(loginState.password)
		if (emailError != null || passwordError != null) {
			updateState { it.copy(emailError = emailError, passwordError = passwordError, credentialsError = null) }
			return
		}

		viewModelScope.launch {
			updateState { it.copy(isLoading = true, credentialsError = null) }
			val success = authRepository.signIn(loginState.email.trim(), loginState.password)
			updateState { it.copy(isLoading = false, isLoggedIn = success,
				credentialsError = if (success) null else R.string.invalid_credentials_error) }
		}
	}
}
