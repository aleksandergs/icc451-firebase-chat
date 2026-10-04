package pucmm.args.icc451_firebase_chat.ui.auth

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.databinding.FragmentLoginBinding
import pucmm.args.icc451_firebase_chat.ui.base.BaseFragment
import pucmm.args.icc451_firebase_chat.ui.main.MainActivity

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

	private val viewModel: LoginViewModel by viewModels()

	override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLoginBinding.inflate(inflater, container, false)

	override fun setUpViews() {
		binding.emailEditText.doAfterTextChanged { viewModel.onEmailChange(it?.toString().orEmpty()) }
		binding.passwordEditText.doAfterTextChanged { viewModel.onPasswordChange(it?.toString().orEmpty()) }

		binding.loginButton.setOnClickListener { viewModel.login() }
		binding.goToRegisterButton.setOnClickListener {
			parentFragmentManager.commit {
				replace(R.id.mainFragmentContainer, RegisterFragment())
			}
		}

		collectWhileStarted(viewModel.uiState) { render(it) }
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
}