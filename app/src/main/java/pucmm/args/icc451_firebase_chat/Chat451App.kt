package pucmm.args.icc451_firebase_chat

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import pucmm.args.icc451_firebase_chat.data.repository.AuthRepository
import pucmm.args.icc451_firebase_chat.data.repository.ChatRepository
import pucmm.args.icc451_firebase_chat.data.repository.StorageRepository
import pucmm.args.icc451_firebase_chat.data.repository.UserRepository
import pucmm.args.icc451_firebase_chat.notifications.Notifications

class Chat451App : Application() {

	override fun onCreate() {
		super.onCreate()
		Notifications.createChannel(this)

		// El token se necesita para enviarle una notificación a este dispositivo
		FirebaseMessaging.getInstance().token.addOnSuccessListener { token -> Log.d("Chat451App", "FCM token: $token") }
	}

	companion object {
		// Se crean una sola vez para toda la app
		// Son lazy porque FirebaseApp se inicializa despues de cargar Application
		val authRepository by lazy { AuthRepository() }

		val userRepository by lazy { UserRepository() }

		val chatRepository by lazy { ChatRepository() }

		val storageRepository by lazy { StorageRepository() }

		// Para trabajo que debe sobrevivir a la pantalla que lo pidio
		val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
	}
}