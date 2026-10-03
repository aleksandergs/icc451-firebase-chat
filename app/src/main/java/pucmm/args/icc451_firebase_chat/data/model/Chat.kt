package pucmm.args.icc451_firebase_chat.data.model

data class Chat(
	var id: Long,
	var members: Pair<Long, Long>,
	var lastMessage: String,
	var lastMessageTimestamp: Long,
	var lastMessageUserId: Long
)
