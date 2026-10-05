package pucmm.args.icc451_firebase_chat.ui.main.users

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.data.model.User
import pucmm.args.icc451_firebase_chat.data.repository.AuthRepository
import pucmm.args.icc451_firebase_chat.data.repository.UserRepository
import pucmm.args.icc451_firebase_chat.ui.base.BaseViewModel

data class UsersUiState(
	val users: List<User> = emptyList(),
	val isLoading: Boolean = false,
)

class UsersViewModel(
	private val userRepository: UserRepository = UserRepository(),
	private val currentUserId: Long = AuthRepository().currentUserId,
) : BaseViewModel<UsersUiState>(UsersUiState()) {

	fun loadUsers() {
		viewModelScope.launch {
			updateState { it.copy(isLoading = true) }
			val users = userRepository.getAllUsers().filter { it.id != currentUserId }
			updateState { it.copy(users = users, isLoading = false) }
		}
	}

	fun getUser(id: Long) {
		viewModelScope.launch {
			updateState { it.copy(isLoading = true) }
			val user = userRepository.getUser(id)
			updateState { if (user != null) it.copy(users = listOf(user), isLoading = false) else it.copy(isLoading = false) }
		}
	}

}