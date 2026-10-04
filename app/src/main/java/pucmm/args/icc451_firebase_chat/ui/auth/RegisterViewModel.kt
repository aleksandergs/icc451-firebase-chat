package pucmm.args.icc451_firebase_chat.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.data.model.User
import pucmm.args.icc451_firebase_chat.data.repository.AuthRepository
import pucmm.args.icc451_firebase_chat.data.repository.UserRepository
import pucmm.args.icc451_firebase_chat.utils.ValidationUtils

data class RegisterUiState(
	val nickname: String = "",
	val email: String = "",
	val password: String = "",
	val confirmPassword: String = "",
	val nicknameError: Int? = null,
	val emailError: Int? = null,
	val passwordError: Int? = null,
	val confirmPasswordError: Int? = null,
	val isLoading: Boolean = false,
	val errorMessage: Int? = null,
	val isRegistered: Boolean = false,
)

class RegisterViewModel(
	private val authRepository: AuthRepository = AuthRepository(),
	private val userRepository: UserRepository = UserRepository(),
) : ViewModel() {

	private val _uiState = MutableStateFlow(RegisterUiState())
	val uiState = _uiState.asStateFlow()

	fun onNicknameChange(nickname: String) {
		_uiState.value = _uiState.value.copy(nickname = nickname, nicknameError = null, errorMessage = null)
	}

	fun onEmailChange(email: String) {
		_uiState.value = _uiState.value.copy(email = email, emailError = null, errorMessage = null)
	}

	fun onPasswordChange(password: String) {
		_uiState.value = _uiState.value.copy(password = password, passwordError = null, errorMessage = null)
	}

	fun onConfirmPasswordChange(confirmPassword: String) {
		_uiState.value = _uiState.value.copy(
			confirmPassword = confirmPassword,
			confirmPasswordError = null,
			errorMessage = null,
		)
	}

	fun onErrorShown() {
		_uiState.value = _uiState.value.copy(errorMessage = null)
	}

	fun register() {
		val registerState = _uiState.value
		val nicknameError = ValidationUtils.validateNickname(registerState.nickname)
		val emailError = ValidationUtils.validateEmail(registerState.email)
		val passwordError = ValidationUtils.validatePassword(registerState.password)
		val confirmPasswordError = ValidationUtils.validateConfirmPassword(registerState.password, registerState.confirmPassword)

		if (nicknameError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
			_uiState.value = registerState.copy(nicknameError = nicknameError, emailError = emailError,
				passwordError = passwordError, confirmPasswordError = confirmPasswordError,
			)
			return
		}

		viewModelScope.launch {
			_uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
			val nickname = registerState.nickname.trim()
			val email = registerState.email.trim()
			val success = authRepository.register(email, registerState.password)
			if (success) {
				userRepository.saveUser(User(nickname = nickname, email = email))
			}
			_uiState.value = _uiState.value.copy(isLoading = false, isRegistered = success,
				errorMessage = if (success) null else R.string.unknown_error
			)
		}
	}
}
