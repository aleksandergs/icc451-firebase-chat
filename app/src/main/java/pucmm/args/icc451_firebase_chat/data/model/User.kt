package pucmm.args.icc451_firebase_chat.data.model

data class User(
	val id: String = "",
	val nickname: String = "",
	val email: String = "",
	val fcmToken: String? = null
)
