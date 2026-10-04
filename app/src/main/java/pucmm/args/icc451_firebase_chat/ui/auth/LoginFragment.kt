package pucmm.args.icc451_firebase_chat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.databinding.FragmentLoginBinding
import pucmm.args.icc451_firebase_chat.ui.main.MainActivity

class LoginFragment : Fragment() {

	private val viewModel: LoginViewModel by viewModels()

	private var _binding: FragmentLoginBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentLoginBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.emailEditText.doAfterTextChanged { viewModel.onEmailChange(it?.toString().orEmpty()) }
		binding.passwordEditText.doAfterTextChanged { viewModel.onPasswordChange(it?.toString().orEmpty()) }

		binding.loginButton.setOnClickListener { viewModel.login() }
		binding.goToRegisterButton.setOnClickListener {
			parentFragmentManager.commit {
				replace(R.id.mainFragmentContainer, RegisterFragment())
			}
		}

		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				viewModel.uiState.collect { render(it) }
			}
		}
	}

	private fun render(state: LoginUiState) {
		binding.emailInputLayout.error = (state.emailError ?: state.credentialsError)?.let(::getString)
		binding.passwordInputLayout.error = (state.passwordError ?: state.credentialsError)?.let(::getString)
		binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
		binding.loginButton.isEnabled = !state.isLoading
		binding.goToRegisterButton.isEnabled = !state.isLoading

		if (state.isLoggedIn) {
			startActivity(Intent(requireContext(), MainActivity::class.java))
			requireActivity().finish()
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}