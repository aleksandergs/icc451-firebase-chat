package pucmm.args.icc451_firebase_chat.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

	private var _binding: VB? = null
	protected val binding get() = _binding!!

	// Determina si se aplica el padding inferior de los insets del sistema (debe ser false para BottomNavigationView)
	protected open val applyBottomInset: Boolean = true

	protected abstract fun inflateBinding(inflater: LayoutInflater): VB

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		_binding = inflateBinding(layoutInflater)
		setContentView(binding.root)
		ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			view.setPadding(
				systemBars.left,
				systemBars.top,
				systemBars.right,
				if (applyBottomInset) systemBars.bottom else 0,
			)
			insets
		}
	}

	protected fun <T> collectWhileStarted(flow: StateFlow<T>, render: (T) -> Unit) {
		lifecycleScope.launch {
			repeatOnLifecycle(Lifecycle.State.STARTED) {
				flow.collect { render(it) }
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		_binding = null
	}
}
