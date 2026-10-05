package pucmm.args.icc451_firebase_chat.ui.main.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.data.model.User
import pucmm.args.icc451_firebase_chat.databinding.FragmentUsersBinding
import pucmm.args.icc451_firebase_chat.ui.base.BaseFragment
import pucmm.args.icc451_firebase_chat.ui.chat.ChatActivity

class UsersFragment : BaseFragment<FragmentUsersBinding>() {

	private val viewModel: UsersViewModel by viewModels()
	private val userAdapter = UsersAdapter { user -> openChat(user) }

	override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentUsersBinding.inflate(inflater, container, false)

	override fun setUpViews() {
		binding.usersRecyclerView.adapter = userAdapter
		binding.retryButton.setOnClickListener { viewModel.loadUsers() }
		viewModel.loadUsers()
		collectWhileStarted(viewModel.uiState) { render(it) }
	}

	private fun render(state: UsersUiState) {
		userAdapter.submitList(state.users)
		binding.loadingSpinner.visibility = if (state.isLoading) View.VISIBLE else View.GONE

		val stateMessage = when {
			state.errorMessage != null -> state.errorMessage
			!state.isLoading && state.users.isEmpty() -> R.string.empty_users
			else -> null
		}
		val retryButton = if (state.errorMessage != null) binding.retryButton else null
		showStateMessage(binding.emptyState, binding.emptyText, stateMessage, retryButton)
	}

	private fun openChat(user: User) {
		ChatActivity.start(requireContext(), user.id)
	}
}