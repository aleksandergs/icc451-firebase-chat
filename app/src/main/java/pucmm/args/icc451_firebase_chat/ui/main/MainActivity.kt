package pucmm.args.icc451_firebase_chat.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import pucmm.args.icc451_firebase_chat.Chat451App
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.databinding.ActivityMainBinding
import pucmm.args.icc451_firebase_chat.notifications.Notifications
import pucmm.args.icc451_firebase_chat.ui.auth.logOut
import pucmm.args.icc451_firebase_chat.ui.base.BaseActivity
import pucmm.args.icc451_firebase_chat.ui.main.chats.ChatsFragment
import pucmm.args.icc451_firebase_chat.ui.main.users.UsersFragment

class MainActivity : BaseActivity<ActivityMainBinding>() {

	override val applyBottomInset = false

	override fun inflateBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)

	// Para mostrar notificaciones en Android 13 en adelante hace falta este permiso
	private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		requestNotificationPermission()

		Notifications.createChannel(this)

		val currentUserId = Chat451App.authRepository.currentUserId
		com.google.firebase.messaging.FirebaseMessaging.getInstance().token
			.addOnCompleteListener { task ->
				if (task.isSuccessful && task.result != null) {
					val currentToken = task.result

					// Guardar o fusionar el token en el documento de este usuario
					com.google.firebase.firestore.FirebaseFirestore.getInstance()
						.collection("users")
						.document(currentUserId)
						.set(
							mapOf("fcmToken" to currentToken),
							com.google.firebase.firestore.SetOptions.merge()
						)
				}
			}

		binding.toolbar.setOnMenuItemClickListener { item ->
			when (item.itemId) {
				R.id.action_logout -> {
					logOut()
					true
				}
				else -> false
			}
		}

		// Carga la pantalla de chats por defecto, si no hay un estado anterior
		if (savedInstanceState == null) {
			supportFragmentManager.commit { replace(R.id.mainFragmentContainer, ChatsFragment()) }
		}

		// Configura la barra de navegacion para cambiar entre (Chats, Users) fragments
		binding.bottomNavigation.setOnItemSelectedListener { item ->
			val fragment: Fragment = when (item.itemId) {
				R.id.nav_chats -> ChatsFragment()
				R.id.nav_users -> UsersFragment()
				else -> return@setOnItemSelectedListener false
			}
			supportFragmentManager.commit { replace(R.id.mainFragmentContainer, fragment) }
			true
		}
	}

	private fun requestNotificationPermission() {
		val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
			ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
		if (needsPermission)
			notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
	}
}