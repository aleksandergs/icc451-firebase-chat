package pucmm.args.icc451_firebase_chat.ui.main.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import pucmm.args.icc451_firebase_chat.databinding.FragmentUsersBinding
import pucmm.args.icc451_firebase_chat.ui.base.BaseFragment

class UsersFragment : BaseFragment<FragmentUsersBinding>() {

	private val viewModel: UsersViewModel by viewModels()
	private val userAdapter = UsersAdapter()

	override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentUsersBinding.inflate(inflater, container, false)

	override fun setUpViews() {
		binding.usersRecyclerView.adapter = userAdapter
		viewModel.loadUsers()
		collectWhileStarted(viewModel.uiState) { render(it) }
	}

	private fun render(state: UsersUiState) {
		userAdapter.submitList(state.users)
		binding.loadingSpinner.visibility = if (state.isLoading || state.users.isEmpty()) View.VISIBLE else View.GONE
	}
}