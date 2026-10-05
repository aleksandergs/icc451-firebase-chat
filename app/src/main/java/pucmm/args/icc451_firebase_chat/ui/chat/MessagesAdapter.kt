package pucmm.args.icc451_firebase_chat.ui.chat

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.android.material.color.MaterialColors
import pucmm.args.icc451_firebase_chat.data.model.Message
import pucmm.args.icc451_firebase_chat.databinding.ItemMessageBinding
import pucmm.args.icc451_firebase_chat.utils.TimeUtils

class MessagesAdapter(
	private val currentUserId: String,
) : Adapter<MessagesAdapter.MessageViewHolder>() {

	private var messages: List<Message> = emptyList()

	fun submitList(newMessages: List<Message>) {
		messages = newMessages
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
		val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MessageViewHolder(binding, currentUserId)
	}

	override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
		holder.bind(messages[position])
	}

	override fun getItemCount(): Int = messages.size

	class MessageViewHolder(
		private val binding: ItemMessageBinding,
		private val currentUserId: String,
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(message: Message) {
			binding.messageText.text = message.text
			binding.timeText.text = TimeUtils.formatTimestamp(message.timestamp)

			// Determina el color y la posicion del mensaje segun si es del usuario o no
			val isMine = message.senderId == currentUserId
			val color = if (isMine) {
				com.google.android.material.R.attr.colorPrimaryContainer
			} else {
				com.google.android.material.R.attr.colorSurfaceVariant
			}
			binding.messageCard.setCardBackgroundColor(
				MaterialColors.getColor(binding.messageCard, color)
			)

			val params = binding.messageCard.layoutParams as FrameLayout.LayoutParams
			params.gravity = if (isMine) Gravity.END else Gravity.START
			binding.messageCard.layoutParams = params
		}
	}
}
