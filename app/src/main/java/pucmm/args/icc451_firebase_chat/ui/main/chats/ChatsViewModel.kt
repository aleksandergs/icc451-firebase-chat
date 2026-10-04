package pucmm.args.icc451_firebase_chat.ui.main.chats

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.data.repository.AuthRepository
import pucmm.args.icc451_firebase_chat.data.repository.ChatRepository
import pucmm.args.icc451_firebase_chat.data.repository.UserRepository
import pucmm.args.icc451_firebase_chat.ui.base.BaseViewModel

data class ChatItem(
	val otherUserId: Long,
	val nickname: String,
	val lastMessage: String,
	val timestamp: Long,
)

data class ChatsUiState(
	val chats: List<ChatItem> = emptyList(),
	val isLoading: Boolean = false,
)

class ChatsViewModel(
	private val chatRepository: ChatRepository = ChatRepository,
	private val userRepository: UserRepository = UserRepository(),
	private val currentUserId: Long = AuthRepository().currentUserId,
) : BaseViewModel<ChatsUiState>(ChatsUiState()) {

	fun loadChats() {
		viewModelScope.launch {
			updateState { it.copy(chats = emptyList(), isLoading = true) }
			val chats = chatRepository.getChats(currentUserId).map { chat ->
				val otherUserId = chat.membersIDs.firstOrNull { it != currentUserId } ?: 0L
				val nickname = userRepository.getUser(otherUserId)?.nickname
				ChatItem(otherUserId = otherUserId, nickname = nickname.orEmpty(),
					lastMessage = chat.lastMessage, timestamp = chat.lastMessageTimestamp,)
			}
			updateState { it.copy(chats = chats, isLoading = false) }
		}
	}
}