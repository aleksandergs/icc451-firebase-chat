package pucmm.args.icc451_firebase_chat.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

	private var _binding: VB? = null
	protected val binding get() = _binding!!

	protected abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

	protected abstract fun setUpViews()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = inflateBinding(inflater, container)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setUpViews()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	protected fun <T> collectWhileStarted(flow: StateFlow<T>, render: (T) -> Unit) {
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				flow.collect { render(it) }
			}
		}
	}

	//
	protected fun showStateMessage(container: View, text: TextView, stateMessage: Int?, retryButton: View?) {
		if (stateMessage == null) {
			container.visibility = View.GONE
			return
		}
		container.visibility = View.VISIBLE
		text.setText(stateMessage)
		retryButton?.visibility = View.VISIBLE
	}
}
