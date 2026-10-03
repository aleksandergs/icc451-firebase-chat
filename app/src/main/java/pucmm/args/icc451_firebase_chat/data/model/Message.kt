package pucmm.args.icc451_firebase_chat.data.model

data class Message(
	val id: Long = 0,
	val chatId: Long = 0,
	val senderId: Long = 0,
	val text: String = "",
	val timestamp: Long = 0
)
