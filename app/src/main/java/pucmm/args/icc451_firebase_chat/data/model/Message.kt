package pucmm.args.icc451_firebase_chat.data.model

enum class MessageType {
	TEXT,
	IMAGE,
	IMAGE_TEXT
}

data class Message(
	val id: String = "",
	val chatId: String = "",
	val senderId: String = "",
	val text: String = "",
	val type: MessageType = MessageType.TEXT,
	val imageUrl: String? = null,
	val timestamp: Long = 0
)
