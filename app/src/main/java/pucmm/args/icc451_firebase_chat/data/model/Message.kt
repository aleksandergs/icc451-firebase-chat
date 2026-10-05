package pucmm.args.icc451_firebase_chat.data.model

data class Message(
	val id: String = "",
	val chatId: String = "",
	val senderId: String = "",
	val text: String = "",
	val timestamp: Long = 0
)
