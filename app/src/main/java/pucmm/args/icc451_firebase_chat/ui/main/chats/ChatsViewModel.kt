package pucmm.args.icc451_firebase_chat.ui.main.chats

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
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

	// Evita abrir varios listeners si la pantalla se reanuda o se reintenta
	private var observing = false

	// Los chats llegan en tiempo real, así que se observan una sola vez
	fun observeChats() {
		if (observing) return
		observing = true
		updateState { it.copy(isLoading = true, errorMessage = null) }

		viewModelScope.launch {
			chatRepository.getChats(currentUserId)
				.catch {
					// Permite volver a intentar con el botón de reintentar
					observing = false
					updateState { it.copy(isLoading = false, errorMessage = R.string.network_error) }
				}
				.collect { chats ->
					val items = chats.map { chat ->
						val otherUserId = chat.membersIDs.firstOrNull { it != currentUserId }.orEmpty()
						ChatItem(otherUserId = otherUserId, nickname = nicknameOf(otherUserId),
							lastMessage = chat.lastMessage, timestamp = chat.lastMessageTimestamp)
					}
					updateState { it.copy(chats = items, isLoading = false) }
				}
		}
	}

	// El nombre del otro usuario se pide una sola vez por chat
	private val nicknames = mutableMapOf<String, String>()

	private suspend fun nicknameOf(userId: String): String {
		nicknames[userId]?.let { return it }
		val nickname = userRepository.getUser(userId)?.nickname.orEmpty()
		nicknames[userId] = nickname
		return nickname
	}
}