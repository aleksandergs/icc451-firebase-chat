package pucmm.args.icc451_firebase_chat.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pucmm.args.icc451_firebase_chat.R

class ChatMessagingService : FirebaseMessagingService() {

	// El token identifica este dispositivo, se necesita para enviarle una notificación
	override fun onNewToken(token: String) {
		Log.d(TAG, "FCM token: $token")
	}

	// Los mensajes con datos llegan aquí, incluso con la app en primer plano.
	// El servidor envía otherUserId, nickname y text
	override fun onMessageReceived(message: RemoteMessage) {
		val data = message.data
		val title = data["nickname"] ?: message.notification?.title ?: getString(R.string.app_name)
		val body = data["text"] ?: message.notification?.body.orEmpty()

		Notifications.showMessage(this, title, body, data["otherUserId"].orEmpty())
	}

	private companion object {
		const val TAG = "ChatMessagingService"
	}
}
