package pucmm.args.icc451_firebase_chat.ui.main.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.data.model.User
import pucmm.args.icc451_firebase_chat.data.repository.UserRepository

class UsersViewModel(
	private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

	private val _users = MutableStateFlow<List<User>>(emptyList())
	val users = _users.asStateFlow()

	fun loadUsers() {
		viewModelScope.launch {
			_users.value = userRepository.getAllUsers()
		}
	}

	fun getUser(id: Long) {
		viewModelScope.launch {
			val user = userRepository.getUser(id)
			user?.let {
				_users.value = listOf(it)
			}
		}
	}

}