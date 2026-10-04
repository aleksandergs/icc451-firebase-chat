package pucmm.args.icc451_firebase_chat.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.data.repository.AuthRepository
import pucmm.args.icc451_firebase_chat.databinding.ActivitySplashBinding
import pucmm.args.icc451_firebase_chat.ui.auth.AuthActivity
import pucmm.args.icc451_firebase_chat.ui.base.BaseActivity
import pucmm.args.icc451_firebase_chat.ui.main.MainActivity
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

	override fun inflateBinding(inflater: LayoutInflater) = ActivitySplashBinding.inflate(inflater)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		lifecycleScope.launch {
			delay(1000.milliseconds)
			goNextScreen()
		}
	}

	private fun goNextScreen() {
		val authRepository = AuthRepository()
		val destination = if (authRepository.isUserLoggedIn()) {
			MainActivity::class.java
		} else {
			AuthActivity::class.java
		}

		val intent = Intent(this, destination)
		startActivity(intent)
		finish()
	}

}