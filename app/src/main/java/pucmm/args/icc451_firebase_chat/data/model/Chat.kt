package pucmm.args.icc451_firebase_chat.data.model

data class Chat(
	val id: Long,
	val membersIDs: Set<Long>,
	val lastReadTimestamps: Map<Long, Long>,
	val lastMessage: String,
	val lastMessageTimestamp: Long,
	val lastMessageUserId: Long
)
