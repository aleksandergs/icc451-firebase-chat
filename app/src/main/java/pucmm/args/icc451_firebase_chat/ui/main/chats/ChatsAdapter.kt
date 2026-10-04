package pucmm.args.icc451_firebase_chat.ui.main.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import pucmm.args.icc451_firebase_chat.databinding.ItemChatBinding

class ChatsAdapter(
	private val onChatClick: (ChatItem) -> Unit,
) : Adapter<ChatsAdapter.ChatViewHolder>() {

	private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

	private var chats: List<ChatItem> = emptyList()

	fun submitList(newChats: List<ChatItem>) {
		chats = newChats
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
		val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ChatViewHolder(binding, timeFormat, onChatClick)
	}

	override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
		holder.bind(chats[position])
	}

	override fun getItemCount(): Int = chats.size

	class ChatViewHolder(
		private val binding: ItemChatBinding,
		private val timeFormat: SimpleDateFormat,
		private val onChatClick: (ChatItem) -> Unit,
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(chat: ChatItem) {
			binding.nickText.text = chat.nickname
			binding.lastMessageText.text = chat.lastMessage
			binding.timeText.text = timeFormat.format(Date(chat.timestamp))
			binding.root.setOnClickListener { onChatClick(chat) }
		}
	}
}