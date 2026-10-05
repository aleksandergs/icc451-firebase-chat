package pucmm.args.icc451_firebase_chat.ui.main.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.databinding.FragmentChatsBinding
import pucmm.args.icc451_firebase_chat.ui.base.BaseFragment
import pucmm.args.icc451_firebase_chat.ui.chat.ChatActivity

class ChatsFragment : BaseFragment<FragmentChatsBinding>() {

	private val viewModel: ChatsViewModel by viewModels()
	private val chatsAdapter = ChatsAdapter { chat -> openChat(chat) }

	override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
		FragmentChatsBinding.inflate(inflater, container, false)

	override fun setUpViews() {
		binding.chatsRecyclerView.adapter = chatsAdapter
		binding.retryButton.setOnClickListener { viewModel.loadChats() }
		collectWhileStarted(viewModel.uiState) { render(it) }
	}

	override fun onStart() {
		super.onStart()
		viewModel.loadChats()
	}

	private fun render(state: ChatsUiState) {
		chatsAdapter.submitList(state.chats)
		binding.loadingSpinner.visibility = if (state.isLoading) View.VISIBLE else View.GONE

		val stateMessage = when {
			state.errorMessage != null -> state.errorMessage
			!state.isLoading && state.chats.isEmpty() -> R.string.empty_chats
			else -> null
		}
		val retryButton = if (state.errorMessage != null) binding.retryButton else null
		showStateMessage(binding.emptyState, binding.emptyText, stateMessage, retryButton)
	}

	private fun openChat(chat: ChatItem) {
		ChatActivity.start(requireContext(), chat.otherUserId)
	}
}