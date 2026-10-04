package pucmm.args.icc451_firebase_chat.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.data.repository.AuthRepository
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
	private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {

	private val _uiState = MutableStateFlow(LoginUiState())
	val uiState = _uiState.asStateFlow()

	fun onEmailChange(email: String) {
		_uiState.value = _uiState.value.copy(email = email, emailError = null, credentialsError = null)
	}

	fun onPasswordChange(password: String) {
		_uiState.value = _uiState.value.copy(password = password, passwordError = null, credentialsError = null)
	}

	fun login() {
		val loginState = uiState.value
		val emailError = ValidationUtils.validateEmail(loginState.email)
		val passwordError = ValidationUtils.validatePassword(loginState.password)
		if (emailError != null || passwordError != null) {
			_uiState.value = loginState.copy(emailError = emailError, passwordError = passwordError, credentialsError = null)
			return
		}

		viewModelScope.launch {
			_uiState.value = _uiState.value.copy(isLoading = true, credentialsError = null)
			val success = authRepository.signIn(loginState.email.trim(), loginState.password)
			_uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = success,
				credentialsError = if (success) null else R.string.invalid_credentials_error)
		}
	}
}
