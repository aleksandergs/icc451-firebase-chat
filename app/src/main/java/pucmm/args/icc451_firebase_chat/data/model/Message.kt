package pucmm.args.icc451_firebase_chat.data.model

data class Message(
	var id: Long = 0,
	var senderId: Long = 0,
	var text: String = "",
	var timestamp: Long = 0
)
