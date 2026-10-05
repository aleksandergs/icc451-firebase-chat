package pucmm.args.icc451_firebase_chat

import android.app.Application
import pucmm.args.icc451_firebase_chat.data.repository.AuthRepository
import pucmm.args.icc451_firebase_chat.data.repository.ChatRepository
import pucmm.args.icc451_firebase_chat.data.repository.UserRepository

class Chat451App : Application() {

	companion object {
		// Se crean una sola vez para toda la app, así los ViewModels comparten las
		// mismas instancias y Firebase se conecta desde aquí
		val authRepository = AuthRepository()

		val userRepository = UserRepository()

		val chatRepository = ChatRepository()
	}
}