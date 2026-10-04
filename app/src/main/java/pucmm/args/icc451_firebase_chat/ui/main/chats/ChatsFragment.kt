package pucmm.args.icc451_firebase_chat.ui.main.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import pucmm.args.icc451_firebase_chat.databinding.FragmentChatsBinding
import pucmm.args.icc451_firebase_chat.ui.base.BaseFragment

class ChatsFragment : BaseFragment<FragmentChatsBinding>() {

	private val viewModel: ChatsViewModel by viewModels()
	private val chatsAdapter = ChatsAdapter { chat -> openChat(chat) }

	override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
		FragmentChatsBinding.inflate(inflater, container, false)

	override fun setUpViews() {
		binding.chatsRecyclerView.adapter = chatsAdapter
		collectWhileStarted(viewModel.uiState) { render(it) }
	}

	override fun onStart() {
		super.onStart()
		viewModel.loadChats()
	}

	private fun render(state: ChatsUiState) {
		chatsAdapter.submitList(state.chats)
		binding.loadingSpinner.visibility =
			if (state.isLoading || state.chats.isEmpty()) View.VISIBLE else View.GONE
	}

	private fun openChat(chat: ChatItem) {
		// cambia a chatActivity
	}
}