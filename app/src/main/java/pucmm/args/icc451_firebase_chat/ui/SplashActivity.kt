package pucmm.args.icc451_firebase_chat.ui

import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.data.repository.AuthRepository
import pucmm.args.icc451_firebase_chat.databinding.ActivitySplashBinding
import pucmm.args.icc451_firebase_chat.ui.auth.AuthActivity
import pucmm.args.icc451_firebase_chat.ui.main.MainActivity
import kotlin.time.Duration.Companion.milliseconds

class SplashActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: android.os.Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		val binding = ActivitySplashBinding.inflate(layoutInflater)
		setContentView(binding.root)
		ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}

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