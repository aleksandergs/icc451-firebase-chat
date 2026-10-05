package pucmm.args.icc451_firebase_chat.ui.chat

import android.net.Uri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.Chat451App
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.data.model.Message
import pucmm.args.icc451_firebase_chat.data.repository.ChatRepository
import pucmm.args.icc451_firebase_chat.data.repository.StorageRepository
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
	private val storageRepository: StorageRepository = Chat451App.storageRepository,
	val currentUserId: String = Chat451App.authRepository.currentUserId,
) : BaseViewModel<ChatUiState>(ChatUiState()) {

	fun openChat(otherUserId: String) {
		viewModelScope.launch {
			updateState { it.copy(isLoading = true, errorMessage = null) }
			try {
				val chatId = chatRepository.getOrCreateChat(currentUserId, otherUserId)
				val nickname = userRepository.getUser(otherUserId)?.nickname.orEmpty()
				updateState { it.copy(chatId = chatId, nickname = nickname) }
				observeMessages(chatId)
			} catch (e: Exception) {
				updateState { it.copy(isLoading = false, errorMessage = R.string.network_error) }
			}
		}
	}

	// Los mensajes llegan en tiempo real
	private fun observeMessages(chatId: String) {
		viewModelScope.launch {
			chatRepository.getMessages(chatId)
				.catch { updateState { it.copy(isLoading = false, errorMessage = R.string.network_error) } }
				.collect { messages -> updateState { it.copy(messages = messages, isLoading = false) } }
		}
	}

	fun sendMessage(text: String, imageUri: String?) {
		val chatId = currentState.chatId ?: return

		if (text.isBlank() && imageUri == null) {
			updateState { it.copy(errorMessage = R.string.empty_message_error) }
			return
		}

		viewModelScope.launch {
			try {
				// Si hay imagen se sube primero a Storage y se guarda su URL
				val imageUrl = imageUri?.let { storageRepository.uploadImage(chatId, it) }
				chatRepository.sendMessage(chatId, currentUserId, text.trim(), imageUrl)
			} catch (e: Exception) {
				updateState { it.copy(errorMessage = R.string.network_error) }
			}
		}
	}

	// Limpia el mensaje de error después de que se muestre en la UI
	fun onErrorShown() {
		updateState { it.copy(errorMessage = null) }
	}

	// Elimina el chat si está vacío al cerrar la actividad. Usa el scope de la app
	// porque el scope del ViewModel ya está cancelado en este punto
	override fun onCleared() {
		val chatId = currentState.chatId ?: return
		Chat451App.applicationScope.launch { chatRepository.deleteChatIfEmpty(chatId) }
	}
}
