package pucmm.args.icc451_firebase_chat.ui.main.users

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.Chat451App
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.data.model.User
import pucmm.args.icc451_firebase_chat.data.repository.UserRepository
import pucmm.args.icc451_firebase_chat.ui.base.BaseViewModel

data class UsersUiState(
	val users: List<User> = emptyList(),
	val isLoading: Boolean = false,
	val errorMessage: Int? = null,
)

class UsersViewModel(
	private val userRepository: UserRepository = Chat451App.userRepository,
	private val currentUserId: String = Chat451App.authRepository.currentUserId,
) : BaseViewModel<UsersUiState>(UsersUiState()) {

	fun loadUsers() {
		viewModelScope.launch {
			updateState { it.copy(isLoading = true, errorMessage = null) }
			try {
				val users = userRepository.getAllUsers().filter { it.id != currentUserId }
				updateState { it.copy(users = users, isLoading = false) }
			} catch (e: Exception) {
				updateState { it.copy(isLoading = false, errorMessage = R.string.network_error) }
			}
		}
	}

	fun getUser(id: String) {
		viewModelScope.launch {
			updateState { it.copy(isLoading = true, errorMessage = null) }
			try {
				val user = userRepository.getUser(id)
				updateState {
					if (user != null) it.copy(users = listOf(user), isLoading = false)
					else it.copy(isLoading = false)
				}
			} catch (e: Exception) {
				updateState { it.copy(isLoading = false, errorMessage = R.string.network_error) }
			}
		}
	}

}