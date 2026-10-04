package pucmm.args.icc451_firebase_chat.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

	private var _binding: VB? = null
	protected val binding get() = _binding!!

	protected abstract fun inflateBinding(inflater: LayoutInflater): VB

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		_binding = inflateBinding(layoutInflater)
		setContentView(binding.root)
		ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		_binding = null
	}
}
