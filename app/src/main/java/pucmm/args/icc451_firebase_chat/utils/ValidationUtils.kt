package pucmm.args.icc451_firebase_chat.utils

import pucmm.args.icc451_firebase_chat.R

internal object ValidationUtils {

	private const val MIN_PASSWORD_LENGTH = 6
	private val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
	private val nicknamePattern = Regex("^[A-Za-z0-9._-]+$")

	fun validateEmail(email: String): Int? = when {
		email.isBlank() -> R.string.email_required_error
		!emailPattern.matches(email.trim()) -> R.string.email_invalid_error
		else -> null
	}

	fun validatePassword(password: String): Int? = when {
		password.isEmpty() -> R.string.password_required_error
		password.length < MIN_PASSWORD_LENGTH -> R.string.password_short_error
		else -> null
	}

	fun validateConfirmPassword(password: String, confirmPassword: String): Int? = when {
		confirmPassword.isEmpty() -> R.string.password_required_error
		confirmPassword != password -> R.string.password_mismatch_error
		else -> null
	}

	fun validateNickname(nickname: String): Int? = when {
		nickname.isBlank() -> R.string.nick_required_error
		!nicknamePattern.matches(nickname.trim()) -> R.string.nick_invalid_error
		else -> null
	}
}
