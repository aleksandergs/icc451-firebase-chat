package pucmm.args.icc451_firebase_chat.ui.chat

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import kotlinx.coroutines.launch
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.databinding.ActivityChatBinding
import pucmm.args.icc451_firebase_chat.databinding.DialogImagePreviewBinding
import pucmm.args.icc451_firebase_chat.ui.base.BaseActivity
import pucmm.args.icc451_firebase_chat.utils.MediaUtils

class ChatActivity : BaseActivity<ActivityChatBinding>() {

	// Permite iniciar la actividad con el ID del otro usuario como argumento.
	companion object {
		const val EXTRA_OTHER_USER_ID = "extra_other_user_id"

		fun start(context: Context, otherUserId: String) {
			val intent = Intent(context, ChatActivity::class.java)
			intent.putExtra(EXTRA_OTHER_USER_ID, otherUserId)
			context.startActivity(intent)
		}
	}

	private val viewModel: ChatViewModel by viewModels()
	private lateinit var messagesAdapter: MessagesAdapter

	// Almacena el uri de la imagen cargada de la galeria
	private var draftImageUri: Uri? = null
	private val imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
		draftImageUri = uri
		renderDraftImage()
	}

	// En Android 9 y anteriores guardar en la galería necesita permiso
	private var pendingDownloadUrl: String? = null
	private val storagePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
		val imageUrl = pendingDownloadUrl
		pendingDownloadUrl = null
		if (granted && imageUrl != null) {
			downloadImage(imageUrl)
		} else if (!granted) {
			Toast.makeText(this, R.string.permission_error, Toast.LENGTH_LONG).show()
		}
	}

	override fun inflateBinding(inflater: LayoutInflater) = ActivityChatBinding.inflate(inflater)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val otherUserId = intent.getStringExtra(EXTRA_OTHER_USER_ID).orEmpty()
		messagesAdapter = MessagesAdapter(viewModel.currentUserId) { imageUrl -> showImagePreview(imageUrl) }

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
		binding.attachImageButton.setOnClickListener {
			imagePicker.launch(
				PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
			)
		}
		binding.removeImageButton.setOnClickListener {
			draftImageUri = null
			renderDraftImage()
		}

		viewModel.openChat(otherUserId)
		collectWhileStarted(viewModel.uiState) { render(it) }
	}

	private fun sendMessage() {
		// El ViewModel avisa con un error si no hay ni texto ni imagen
		viewModel.sendMessage(binding.messageEditText.text?.toString().orEmpty(), draftImageUri?.toString())
		draftImageUri = null
		renderDraftImage()
		binding.messageEditText.text?.clear()
	}

	// Muestra la imagen elegida encima del campo de texto, o esconde la fila si no hay ninguna
	private fun renderDraftImage() {
		binding.previewRow.visibility = if (draftImageUri == null) View.GONE else View.VISIBLE
		binding.previewImage.load(draftImageUri)
	}

	// Modal con la imagen completa escalada para caber en la pantalla
	private fun showImagePreview(imageUrl: String) {
		val dialogBinding = DialogImagePreviewBinding.inflate(layoutInflater)
		val dialog = Dialog(this)
		dialog.setContentView(dialogBinding.root)
		dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

		dialogBinding.fullImage.load(imageUrl)
		dialogBinding.downloadButton.setOnClickListener { downloadImage(imageUrl) }
		dialogBinding.closeButton.setOnClickListener { dialog.dismiss() }
		// Oscurece la pantalla detrás del modal
		dialog.window?.setDimAmount(0.8f)

		dialog.show()
	}

	private fun downloadImage(imageUrl: String) {
		val needsPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
			ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
			PackageManager.PERMISSION_GRANTED
		if (needsPermission) {
			pendingDownloadUrl = imageUrl
			storagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
			return
		}

		lifecycleScope.launch {
			val saved = MediaUtils.saveImage(this@ChatActivity, imageUrl)
			val messageRes = if (saved) R.string.image_saved else R.string.image_save_error
			Toast.makeText(this@ChatActivity, messageRes, Toast.LENGTH_LONG).show()
		}
	}

	private fun render(state: ChatUiState) {
		binding.toolbar.title = state.nickname
		messagesAdapter.submitList(state.messages)
		binding.loadingSpinner.visibility = if (state.isLoading) View.VISIBLE else View.GONE
		if (state.messages.isNotEmpty()) {
			binding.messagesRecyclerView.scrollToPosition(state.messages.lastIndex)
		}

		state.errorMessage?.let { stateMessage ->
			Toast.makeText(this, stateMessage, Toast.LENGTH_LONG).show()
			viewModel.onErrorShown()
		}
	}
}
