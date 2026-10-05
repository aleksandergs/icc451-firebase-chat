package pucmm.args.icc451_firebase_chat.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import pucmm.args.icc451_firebase_chat.databinding.ActivityChatBinding
import pucmm.args.icc451_firebase_chat.ui.base.BaseActivity

class ChatActivity : BaseActivity<ActivityChatBinding>() {

	// Permite iniciar la actividad con el ID del otro usuario como argumento.
	companion object {
		const val EXTRA_OTHER_USER_ID = "extra_other_user_id"

		fun start(context: Context, otherUserId: Long) {
			val intent = Intent(context, ChatActivity::class.java)
			intent.putExtra(EXTRA_OTHER_USER_ID, otherUserId)
			context.startActivity(intent)
		}
	}

	private val viewModel: ChatViewModel by viewModels()
	private lateinit var messagesAdapter: MessagesAdapter

	override fun inflateBinding(inflater: LayoutInflater) = ActivityChatBinding.inflate(inflater)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val otherUserId = intent.getLongExtra(EXTRA_OTHER_USER_ID, 0L)
		messagesAdapter = MessagesAdapter(viewModel.currentUserId)

		binding.messagesRecyclerView.adapter = messagesAdapter
		binding.toolbar.setNavigationOnClickListener { finish() }
		binding.sendButton.setOnClickListener { sendMessage() }
		binding.messageEditText.setOnEditorActionListener { _, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_SEND) {
				sendMessage()
				true
			} else {
				false
			}
		}

		viewModel.openChat(otherUserId)
		collectWhileStarted(viewModel.uiState) { render(it) }
	}

	private fun sendMessage() {
		viewModel.sendMessage(binding.messageEditText.text?.toString().orEmpty())
		binding.messageEditText.text?.clear()
	}

	private fun render(state: ChatUiState) {
		binding.toolbar.title = state.nickname
		messagesAdapter.submitList(state.messages)
		binding.loadingSpinner.visibility = if (state.isLoading) android.view.View.VISIBLE else android.view.View.GONE
		if (state.messages.isNotEmpty()) {
			binding.messagesRecyclerView.scrollToPosition(state.messages.lastIndex)
		}
	}
}
