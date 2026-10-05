package pucmm.args.icc451_firebase_chat.ui.chat

import android.net.Uri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.Chat451App
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.data.model.Message
import pucmm.args.icc451_firebase_chat.data.repository.ChatRepository
import pucmm.args.icc451_firebase_chat.data.repository.UserRepository
import pucmm.args.icc451_firebase_chat.ui.base.BaseViewModel

data class ChatUiState(
	val chatId: String? = null,
	val nickname: String = "",
	val messages: List<Message> = emptyList(),
	val isLoading: Boolean = false,
	val errorMessage: Int? = null,
)

class ChatViewModel(
	private val chatRepository: ChatRepository = Chat451App.chatRepository,
	private val userRepository: UserRepository = Chat451App.userRepository,
	val currentUserId: String = Chat451App.authRepository.currentUserId,
) : BaseViewModel<ChatUiState>(ChatUiState()) {

	fun openChat(otherUserId: String) {
		viewModelScope.launch {
			updateState { it.copy(isLoading = true, errorMessage = null) }
			try {
				val chatId = chatRepository.getOrCreateChat(currentUserId, otherUserId)
				val nickname = userRepository.getUser(otherUserId)?.nickname.orEmpty()
				val messages = chatRepository.getMessages(chatId)
				updateState { it.copy(chatId = chatId, nickname = nickname, messages = messages, isLoading = false) }
			} catch (e: Exception) {
				updateState { it.copy(isLoading = false, errorMessage = R.string.network_error) }
			}
		}
	}

	fun sendMessage(text: String, imageUrl: String?) {
		val chatId = currentState.chatId ?: return

		if (text.isBlank() && imageUrl == null) {
			updateState { it.copy(errorMessage = R.string.empty_message_error) }
			return
		}

		viewModelScope.launch {
			try {
				val message = chatRepository.sendMessage(chatId, currentUserId, text.trim(), imageUrl)
				updateState { it.copy(messages = it.messages + message) }
			} catch (e: Exception) {
				updateState { it.copy(errorMessage = R.string.network_error) }
			}
		}
	}

	// Limpia el mensaje de error después de que se muestre en la UI
	fun onErrorShown() {
		updateState { it.copy(errorMessage = null) }
	}

	// Elimina el chat si está vacío al cerrar la actividad
	override fun onCleared() {
		currentState.chatId?.let { chatRepository.deleteChatIfEmpty(it) }
	}
}
