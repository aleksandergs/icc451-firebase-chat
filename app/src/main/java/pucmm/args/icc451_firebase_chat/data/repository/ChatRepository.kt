package pucmm.args.icc451_firebase_chat.data.repository

import kotlinx.coroutines.delay
import pucmm.args.icc451_firebase_chat.data.model.Chat
import pucmm.args.icc451_firebase_chat.data.model.Message
import kotlin.time.Duration.Companion.milliseconds

class ChatRepository {

	private val chats = mutableListOf<Chat>()
	private val messages = mutableListOf<Message>()

	init {
		val time = System.currentTimeMillis()
		chats += Chat("1", setOf("1", "2"), mapOf("1" to time, "2" to time),
			"World", time, "2")
		chats += Chat("2", setOf("1", "3"), mapOf("1" to time, "3" to time),
			"Slim Shady", time, "3")
		chats += Chat("3", setOf("1", "4"), mapOf("1" to time, "4" to time),
			"SAHUR", time, "4")

		addMessage(chats[0], "1", "Hello")
		addMessage(chats[0], "2", "World")
		addMessage(chats[1], "1", "Bruh")
		addMessage(chats[1], "3", "Slim Shady")
		addMessage(chats[2], "1", "TUNGTUNGTUNGTUNG")
		addMessage(chats[2], "4", "SAHUR")
	}

	suspend fun getChats(userId: String): List<Chat> {
		delay(200.milliseconds)
		return chats.filter { chat -> userId in chat.membersIDs }.sortedByDescending { it.lastMessageTimestamp }
	}

	suspend fun getMessages(chatId: String): List<Message> {
		delay(200.milliseconds)
		return messages.filter { it.chatId == chatId }.sortedBy { it.timestamp }
	}

	suspend fun getOrCreateChat(userId: String, otherUserId: String): String {
		delay(200.milliseconds)
		val existing = chats.firstOrNull { it.membersIDs == setOf(userId, otherUserId) }
		if (existing != null) return existing.id

		val time = System.currentTimeMillis()
		val chat = Chat(id = (chats.size + 1).toString(), membersIDs = setOf(userId, otherUserId),
			lastReadTimestamps = mapOf(userId to time, otherUserId to 0L), lastMessage = "",
			lastMessageTimestamp = 0L, lastMessageUserId = "",
		)
		chats += chat
		return chat.id
	}

	fun deleteChatIfEmpty(chatId: String) {
		if (messages.none { it.chatId == chatId }) {
			chats.removeAll { it.id == chatId }
		}
	}

	suspend fun sendMessage(chatId: String, senderId: String, text: String): Message {
		delay(200.milliseconds)
		return addMessage(chats.first { it.id == chatId }, senderId, text)
	}

	private fun addMessage(chat: Chat, senderId: String, text: String): Message {
		val message = Message((messages.size + 1).toString(), chat.id, senderId, text, System.currentTimeMillis())
		messages += message

		val index = chats.indexOfFirst { it.id == chat.id }
		if (index != -1)
			chats[index] = chats[index].copy(lastMessage = text, lastMessageTimestamp = message.timestamp, lastMessageUserId = senderId)
		return message
	}
}