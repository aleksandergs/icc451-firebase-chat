package pucmm.args.icc451_firebase_chat.ui.auth

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.Chat451App
import pucmm.args.icc451_firebase_chat.data.model.User
import pucmm.args.icc451_firebase_chat.data.repository.AuthRepository
import pucmm.args.icc451_firebase_chat.data.repository.AuthResult
import pucmm.args.icc451_firebase_chat.data.repository.UserRepository
import pucmm.args.icc451_firebase_chat.ui.base.BaseViewModel
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
	private val authRepository: AuthRepository = Chat451App.authRepository,
	private val userRepository: UserRepository = Chat451App.userRepository,
) : BaseViewModel<RegisterUiState>(RegisterUiState()) {

	fun onNicknameChange(nickname: String) {
		updateState { it.copy(nickname = nickname, nicknameError = null, errorMessage = null) }
	}

	fun onEmailChange(email: String) {
		updateState { it.copy(email = email, emailError = null, errorMessage = null) }
	}

	fun onPasswordChange(password: String) {
		updateState { it.copy(password = password, passwordError = null, errorMessage = null) }
	}

	fun onConfirmPasswordChange(confirmPassword: String) {
		updateState { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, errorMessage = null) }
	}

	fun onErrorShown() {
		updateState { it.copy(errorMessage = null) }
	}

	fun register() {
		val registerState = currentState
		val nicknameError = ValidationUtils.validateNickname(registerState.nickname)
		val emailError = ValidationUtils.validateEmail(registerState.email)
		val passwordError = ValidationUtils.validatePassword(registerState.password)
		val confirmPasswordError = ValidationUtils.validateConfirmPassword(registerState.password, registerState.confirmPassword)

		if (nicknameError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
			updateState { it.copy(nicknameError = nicknameError, emailError = emailError,
				passwordError = passwordError, confirmPasswordError = confirmPasswordError) }
			return
		}

		viewModelScope.launch {
			updateState { it.copy(isLoading = true, errorMessage = null) }
			val nickname = registerState.nickname.trim()
			val email = registerState.email.trim()
			when (val result = authRepository.register(email, registerState.password)) {
				is AuthResult.Success -> {
					// El usuario se guarda con el uid que Firebase acaba de crear
					userRepository.saveUser(User(authRepository.currentUserId, nickname, email))
					updateState { it.copy(isLoading = false, isRegistered = true) }
				}
				is AuthResult.Failure ->
					updateState { it.copy(isLoading = false, errorMessage = result.messageRes) }
			}
		}
	}
}
