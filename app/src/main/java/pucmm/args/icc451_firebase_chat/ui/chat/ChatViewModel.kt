package pucmm.args.icc451_firebase_chat.ui.chat

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.data.model.Message
import pucmm.args.icc451_firebase_chat.data.repository.AuthRepository
import pucmm.args.icc451_firebase_chat.data.repository.ChatRepository
import pucmm.args.icc451_firebase_chat.data.repository.UserRepository
import pucmm.args.icc451_firebase_chat.ui.base.BaseViewModel

data class ChatUiState(
	val chatId: Long? = null,
	val nickname: String = "",
	val messages: List<Message> = emptyList(),
	val isLoading: Boolean = false,
)

class ChatViewModel(
	private val chatRepository: ChatRepository = ChatRepository,
	private val userRepository: UserRepository = UserRepository(),
	val currentUserId: Long = AuthRepository().currentUserId,
) : BaseViewModel<ChatUiState>(ChatUiState()) {

	fun openChat(otherUserId: Long) {
		viewModelScope.launch {
			updateState { it.copy(isLoading = true) }
			val chatId = chatRepository.getOrCreateChat(currentUserId, otherUserId)
			val nickname = userRepository.getUser(otherUserId)?.nickname.orEmpty()
			val messages = chatRepository.getMessages(chatId)
			updateState { it.copy(chatId = chatId, nickname = nickname, messages = messages, isLoading = false) }
		}
	}

	fun sendMessage(text: String) {
		if (text.isBlank()) return
		val chatId = currentState.chatId ?: return

		viewModelScope.launch {
			val message = chatRepository.sendMessage(chatId, currentUserId, text)
			updateState { it.copy(messages = it.messages + message) }
		}
	}

	// Elimina el chat si está vacío al cerrar la actividad
	override fun onCleared() {
		currentState.chatId?.let { chatRepository.deleteChatIfEmpty(it) }
	}
}
