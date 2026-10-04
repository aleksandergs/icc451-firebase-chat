package pucmm.args.icc451_firebase_chat.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.databinding.ActivityMainBinding
import pucmm.args.icc451_firebase_chat.ui.base.BaseActivity
import pucmm.args.icc451_firebase_chat.ui.main.chats.ChatsFragment
import pucmm.args.icc451_firebase_chat.ui.main.users.UsersFragment

class MainActivity : BaseActivity<ActivityMainBinding>() {

	override val applyBottomInset = false

	override fun inflateBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

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
}