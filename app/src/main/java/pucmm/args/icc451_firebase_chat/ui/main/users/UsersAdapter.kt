package pucmm.args.icc451_firebase_chat.ui.main.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import pucmm.args.icc451_firebase_chat.data.model.User
import pucmm.args.icc451_firebase_chat.databinding.ItemUserBinding

class UsersAdapter : Adapter<UsersAdapter.UserViewHolder>() {

	private var users: List<User> = emptyList()

	fun submitList(newUsers: List<User>) {
		users = newUsers
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
		val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return UserViewHolder(binding)
	}

	override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
		holder.bind(users[position])
	}

	override fun getItemCount(): Int = users.size

	class UserViewHolder(
		private val binding: ItemUserBinding,
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(user: User) {
			binding.nameText.text = user.nickname
			binding.emailText.text = user.email
		}
	}
}