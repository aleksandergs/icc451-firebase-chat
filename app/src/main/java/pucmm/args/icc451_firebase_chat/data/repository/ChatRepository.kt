package pucmm.args.icc451_firebase_chat.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import pucmm.args.icc451_firebase_chat.data.model.Chat
import pucmm.args.icc451_firebase_chat.data.model.Message
import pucmm.args.icc451_firebase_chat.utils.EnumUtils.messageType
class ChatRepository(
	private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

	// chats/{uidA_uidB}
	private val chatsCollection get() = firestore.collection("chats")

	// el ID del chat corresponde a userA_id + '_' + userB_id, donde los user_id estan ordenados
	// permitiendo que el id del chat sea determinista entre los 2 usuarios
	fun chatIdOf(userId: String, otherUserId: String): String =
		listOf(userId, otherUserId).sorted().joinToString("_")

	// Los chats se actualizan en tiempo real
	fun getChats(userId: String): Flow<List<Chat>> = callbackFlow {
		// Obtenemos el chat del usuario actual
		val registration = chatsCollection.whereArrayContains("members", userId)
			.addSnapshotListener { snapshot, error ->
				if (error != null) {
					close(error)
					return@addSnapshotListener
				}
				// Un chat sin mensajes es un draft, así que no se lista
				val chats = snapshot?.documents.orEmpty().mapNotNull { it.toChat() }
					.sortedByDescending { it.lastMessageTimestamp }
				trySend(chats)
			}
		awaitClose { registration.remove() }
	}

	// Los mensajes de un chat se actualizan en tiempo real
	fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
		val registration = messagesOf(chatId)
			.orderBy("timestamp")
			.addSnapshotListener { snapshot, error ->
				if (error != null) {
					close(error)
					return@addSnapshotListener
				}
				trySend(snapshot?.documents.orEmpty().mapNotNull { it.toMessage() })
			}
		awaitClose { registration.remove() }
	}

	suspend fun getOrCreateChat(userId: String, otherUserId: String): String {
		val chatId = chatIdOf(userId, otherUserId)
		val chatRef = chatsCollection.document(chatId)
		if (!chatRef.get().await().exists()) {
			val chat = Chat(id = chatId, membersIDs = setOf(userId, otherUserId),
				lastReadTimestamps = mapOf(userId to 0L, otherUserId to 0L), lastMessage = "",
				lastMessageTimestamp = 0L, lastMessageUserId = "",
			)
			chatRef.set(chat.toMap()).await()
		}
		return chatId
	}

	suspend fun sendMessage(chatId: String, senderId: String, text: String, imageURL: String? = null): Message {
		require(text.isNotBlank() || imageURL != null) { "Los mensajes deben tener texto o imagen" }

		val chatRef = chatsCollection.document(chatId)
		val messageRef = messagesOf(chatId).document()
		val timestamp = System.currentTimeMillis()
		val message = Message(messageRef.id, chatId, senderId, text, messageType(text, imageURL), imageURL, timestamp)

		val batch = firestore.batch()
		batch.set(messageRef, message.toMap())
		batch.set(chatRef, mapOf("lastMessage" to text, "lastMessageTimestamp" to timestamp,
			"lastMessageUserId" to senderId,), SetOptions.merge())
		batch.commit().await()

		return message
	}

	// Al salir de la pantalla de chat se borra el chat de draft si nunca recibió un mensaje
	suspend fun deleteChatIfEmpty(chatId: String) {
		val chatRef = chatsCollection.document(chatId)
		val messages = messagesOf(chatId).limit(1).get().await()
		if (messages.isEmpty) chatRef.delete().await()
	}

	// chats/{uidA_uidB}/messages/{messageId}
	private fun messagesOf(chatId: String) =
		chatsCollection.document(chatId).collection("messages")
}