package pucmm.args.icc451_firebase_chat.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.Chat451App

fun AppCompatActivity.logOut() {
	lifecycleScope.launch {
		Chat451App.authRepository.signOut()

		val intent = Intent(this@logOut, AuthActivity::class.java)
		// Vacia el stack de actividades para que el usuario no pueda acceder sin iniciar sesión nuevamente
		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		startActivity(intent)
	}
}
