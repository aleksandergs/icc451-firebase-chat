package pucmm.args.icc451_firebase_chat.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import pucmm.args.icc451_firebase_chat.data.model.Chat
import pucmm.args.icc451_firebase_chat.data.model.Message
import pucmm.args.icc451_firebase_chat.data.model.MessageType
import pucmm.args.icc451_firebase_chat.data.model.User

internal fun User.toMap() = mapOf(
	"nickname" to nickname,
	"email" to email,
	"fcmToken" to fcmToken
)

internal fun DocumentSnapshot.toUser(): User? = runCatching {
	User(
		id = id,
		nickname = getString("nickname").orEmpty(),
		email = getString("email").orEmpty(),
		fcmToken = getString("fcmToken").orEmpty(),
	)
}.getOrNull()

internal fun Chat.toMap() = mapOf(
	"members" to membersIDs.toList(),
	"lastMessage" to lastMessage,
	"lastMessageTimestamp" to lastMessageTimestamp,
	"lastMessageUserId" to lastMessageUserId,
	"lastReadTimestamps" to lastReadTimestamps,
)

internal fun DocumentSnapshot.toChat(): Chat? = runCatching {
	Chat(
		id = id,
		membersIDs = extractMembers(),
		lastReadTimestamps = extractReadTimestamps(),
		lastMessage = getString("lastMessage").orEmpty(),
		lastMessageTimestamp = getLong("lastMessageTimestamp") ?: 0L,
		lastMessageUserId = getString("lastMessageUserId").orEmpty(),
	)
}.getOrNull()

private fun DocumentSnapshot.extractMembers(): Set<String> {
	val rawList = get("members") as? List<*>
	return rawList?.filterIsInstance<String>()?.toSet().orEmpty()
}

private fun DocumentSnapshot.extractReadTimestamps(): Map<String, Long> {
	val rawMap = get("lastReadTimestamps") as? Map<*, *> ?: return emptyMap()

	return rawMap.entries.mapNotNull { (key, value) ->
		val stringKey = key as? String ?: return@mapNotNull null
		val longValue = (value as? Number)?.toLong() ?: return@mapNotNull null
		stringKey to longValue
	}.toMap()
}

internal fun Message.toMap() = mapOf(
	"chatId" to chatId,
	"senderId" to senderId,
	"text" to text,
	"type" to type.name,
	"imageUrl" to imageUrl,
	"timestamp" to timestamp,
)

internal fun DocumentSnapshot.toMessage(): Message? = runCatching {
	Message(
		id = id,
		chatId = getString("chatId").orEmpty(),
		senderId = getString("senderId").orEmpty(),
		text = getString("text").orEmpty(),
		type = getString("type")?.let { runCatching { MessageType.valueOf(it) }.getOrNull() }
			?: MessageType.TEXT,
		imageUrl = getString("imageUrl"),
		timestamp = getLong("timestamp") ?: 0L,
	)
}.getOrNull()
