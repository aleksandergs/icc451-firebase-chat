package pucmm.args.icc451_firebase_chat.data.model

data class Chat(
	val id: String,
	val membersIDs: Set<String>,
	val lastReadTimestamps: Map<String, Long>,
	val lastMessage: String,
	val lastMessageTimestamp: Long,
	val lastMessageUserId: String
)
