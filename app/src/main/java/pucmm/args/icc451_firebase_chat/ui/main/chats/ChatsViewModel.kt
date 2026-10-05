package pucmm.args.icc451_firebase_chat.ui.main.chats

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.Chat451App
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.data.repository.ChatRepository
import pucmm.args.icc451_firebase_chat.data.repository.UserRepository
import pucmm.args.icc451_firebase_chat.ui.base.BaseViewModel

data class ChatItem(
	val otherUserId: String,
	val nickname: String,
	val lastMessage: String,
	val timestamp: Long,
)

data class ChatsUiState(
	val chats: List<ChatItem> = emptyList(),
	val isLoading: Boolean = false,
	val errorMessage: Int? = null,
)

class ChatsViewModel(
	private val chatRepository: ChatRepository = Chat451App.chatRepository,
	private val userRepository: UserRepository = Chat451App.userRepository,
	private val currentUserId: String = Chat451App.authRepository.currentUserId,
) : BaseViewModel<ChatsUiState>(ChatsUiState()) {

	fun loadChats() {
		viewModelScope.launch {
			updateState { it.copy(isLoading = true, errorMessage = null) }
			try {
				val chats = chatRepository.getChats(currentUserId).map { chat ->
					val otherUserId = chat.membersIDs.firstOrNull { it != currentUserId }.orEmpty()
					val nickname = userRepository.getUser(otherUserId)?.nickname
					ChatItem(otherUserId = otherUserId, nickname = nickname.orEmpty(),
						lastMessage = chat.lastMessage, timestamp = chat.lastMessageTimestamp)
				}
				updateState { it.copy(chats = chats, isLoading = false) }
			} catch (e: Exception) {
				updateState { it.copy(isLoading = false, errorMessage = R.string.network_error) }
			}
		}
	}
}