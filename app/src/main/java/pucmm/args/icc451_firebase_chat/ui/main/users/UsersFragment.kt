package pucmm.args.icc451_firebase_chat.ui.main.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.databinding.FragmentUsersBinding

class UsersFragment : Fragment() {
	private val viewModel: UsersViewModel by viewModels()
	private val userAdapter = UsersAdapter()

	private var _binding: FragmentUsersBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		_binding = FragmentUsersBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.usersRecyclerView.adapter = userAdapter
		binding.loadingSpinner.visibility = View.VISIBLE
		viewModel.loadUsers()

		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				launch {
					viewModel.users.collect { freshUsers ->
						userAdapter.submitList(freshUsers)
						binding.loadingSpinner.visibility = View.GONE
					}
				}
			}
		}

	}
}