package pucmm.args.icc451_firebase_chat.ui.main.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.data.model.User
import pucmm.args.icc451_firebase_chat.data.repository.UserRepository

data class UsersUiState(
	val users: List<User> = emptyList(),
	val isLoading: Boolean = false,
)

class UsersViewModel(
	private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

	private val _uiState = MutableStateFlow(UsersUiState())
	val uiState = _uiState.asStateFlow()

	fun loadUsers() {
		viewModelScope.launch {
			_uiState.value = _uiState.value.copy(isLoading = true)
			val users = userRepository.getAllUsers()
			_uiState.value = _uiState.value.copy(users = users, isLoading = false)
		}
	}

	fun getUser(id: Long) {
		viewModelScope.launch {
			_uiState.value = _uiState.value.copy(isLoading = true)
			val user = userRepository.getUser(id)
			_uiState.value = if (user != null) {
				_uiState.value.copy(users = listOf(user), isLoading = false)
			} else {
				_uiState.value.copy(isLoading = false)
			}
		}
	}

}