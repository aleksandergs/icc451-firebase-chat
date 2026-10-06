package pucmm.args.icc451_firebase_chat.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pucmm.args.icc451_firebase_chat.Chat451App
import pucmm.args.icc451_firebase_chat.R

class ChatMessagingService : FirebaseMessagingService() {

	// El token identifica este dispositivo, se necesita para enviarle una notificacion
	override fun onNewToken(token: String) {
		val currentUserId = Chat451App.authRepository.currentUserId
		com.google.firebase.firestore.FirebaseFirestore.getInstance()
			.collection("users")
			.document(currentUserId)
			.update("fcmToken", token)
	}

	// Los mensajes con datos llegan aquí, incluso con la app en primer plano
	// El servidor envia otherUserId, nickname y text
	override fun onMessageReceived(message: RemoteMessage) {
		val data = message.data
		val title = data["nickname"] ?: message.notification?.title ?: getString(R.string.app_name)
		val body = data["text"] ?: message.notification?.body.orEmpty()
		val otherUserId = data["otherUserId"].orEmpty()

		if (otherUserId.isNotBlank()) {
			Notifications.showMessage(this, title, body, otherUserId)
		}
	}
}
