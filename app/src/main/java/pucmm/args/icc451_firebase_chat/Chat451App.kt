package pucmm.args.icc451_firebase_chat

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import pucmm.args.icc451_firebase_chat.data.repository.AuthRepository
import pucmm.args.icc451_firebase_chat.data.repository.ChatRepository
import pucmm.args.icc451_firebase_chat.data.repository.StorageRepository
import pucmm.args.icc451_firebase_chat.data.repository.UserRepository

class Chat451App : Application() {

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