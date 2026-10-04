package pucmm.args.icc451_firebase_chat.ui.auth

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.databinding.FragmentRegisterBinding
import pucmm.args.icc451_firebase_chat.ui.base.BaseFragment
import pucmm.args.icc451_firebase_chat.ui.main.MainActivity

class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {

	private val viewModel: RegisterViewModel by viewModels()

	override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentRegisterBinding.inflate(inflater, container, false)

	override fun setUpViews() {
		binding.nickEditText.doAfterTextChanged { viewModel.onNicknameChange(it?.toString().orEmpty()) }
		binding.emailEditText.doAfterTextChanged { viewModel.onEmailChange(it?.toString().orEmpty()) }
		binding.passwordEditText.doAfterTextChanged { viewModel.onPasswordChange(it?.toString().orEmpty()) }
		binding.confirmPasswordEditText.doAfterTextChanged {
			viewModel.onConfirmPasswordChange(it?.toString().orEmpty())
		}

		binding.registerButton.setOnClickListener { viewModel.register() }
		binding.goToLoginButton.setOnClickListener {
			parentFragmentManager.commit {
				replace(R.id.mainFragmentContainer, LoginFragment())
			}
		}

		collectWhileStarted(viewModel.uiState) { render(it) }
	}

	private fun render(state: RegisterUiState) {
		binding.nickInputLayout.error = state.nicknameError?.let(::getString)
		binding.emailInputLayout.error = state.emailError?.let(::getString)
		binding.passwordInputLayout.error = state.passwordError?.let(::getString)
		binding.confirmPasswordInputLayout.error = state.confirmPasswordError?.let(::getString)
		binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
		binding.registerButton.isEnabled = !state.isLoading
		binding.goToLoginButton.isEnabled = !state.isLoading

		state.errorMessage?.let { messageRes ->
			Toast.makeText(requireContext(), messageRes, Toast.LENGTH_LONG).show()
			viewModel.onErrorShown()
		}

		if (state.isRegistered) {
			startActivity(Intent(requireContext(), MainActivity::class.java))
			requireActivity().finish()
		}
	}
}