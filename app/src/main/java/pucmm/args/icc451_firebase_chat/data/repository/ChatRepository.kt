package pucmm.args.icc451_firebase_chat.data.repository

import kotlinx.coroutines.delay
import pucmm.args.icc451_firebase_chat.data.model.Chat
import pucmm.args.icc451_firebase_chat.data.model.Message
import kotlin.time.Duration.Companion.milliseconds

object ChatRepository {

	private val chats = mutableListOf<Chat>()
	private val messages = mutableListOf<Message>()

	init {
		val time = System.currentTimeMillis()
		chats += Chat(1L, setOf(1L, 2L), mapOf(1L to time, 2L to time),
			"World", time, 2L)
		chats += Chat(2L, setOf(1L, 3L), mapOf(1L to time, 3L to time),
			"Slim Shady", time, 3L)
		chats += Chat(3L, setOf(1L, 4L), mapOf(1L to time, 4L to time),
			"SAHUR", time, 4L)

		sendMessage(chats[0], 1L, "Hello")
		sendMessage(chats[0], 2L, "World")
		sendMessage(chats[1], 1L, "Bruh")
		sendMessage(chats[1], 3L, "Slim Shady")
		sendMessage(chats[2], 1L, "TUNGTUNGTUNGTUNG")
		sendMessage(chats[2], 4L, "SAHUR")
	}

	suspend fun getChats(userId: Long): List<Chat> {
		delay(200.milliseconds)
		return chats.filter { chat -> userId in chat.membersIDs }.sortedByDescending { it.lastMessageTimestamp }
	}

	suspend fun getMessages(chatId: Long): List<Message> {
		delay(200.milliseconds)
		return messages.filter { it.chatId == chatId }
	}

	suspend fun getOrCreateChat(userId: Long, otherUserId: Long): Long {
		delay(200.milliseconds)
		val existing = chats.firstOrNull { it.membersIDs == setOf(userId, otherUserId) }
		if (existing != null) return existing.id

		val time = System.currentTimeMillis()
		val chat = Chat(id = chats.size + 1L, membersIDs = setOf(userId, otherUserId),
			lastReadTimestamps = mapOf(userId to time, otherUserId to 0L), lastMessage = "",
			lastMessageTimestamp = 0L, lastMessageUserId = 0L,
		)
		chats += chat
		return chat.id
	}

	fun deleteChatIfEmpty(chatId: Long) {
		if (messages.none { it.chatId == chatId }) {
			chats.removeAll { it.id == chatId }
		}
	}

	fun sendMessage(chat: Chat, senderId: Long, text: String): Message {
		val message = Message(messages.size + 1L, chat.id, senderId, text, System.currentTimeMillis())
		messages += message

		val index = chats.indexOfFirst { it.id == chat.id }
		if (index != -1)
			chats[index] = chats[index].copy(lastMessage = text, lastMessageTimestamp = message.timestamp, lastMessageUserId = senderId)
		return message
	}

	suspend fun sendMessage(chatId: Long, senderId: Long, text: String): Message {
		delay(200.milliseconds)
		return sendMessage(chats.first { it.id == chatId }, senderId, text)
	}
}