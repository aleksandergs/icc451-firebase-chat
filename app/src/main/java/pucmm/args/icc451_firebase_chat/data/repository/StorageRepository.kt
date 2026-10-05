package pucmm.args.icc451_firebase_chat.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import androidx.core.net.toUri

class StorageRepository(
	private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
) {

	suspend fun uploadImage(chatId: String, imageUri: String): String {
		// chats/{chatId}/{uuid}
		val reference = storage.reference.child("chats/$chatId/${UUID.randomUUID()}.jpg")
		reference.putFile(imageUri.toUri()).await()
		return reference.downloadUrl.await().toString()
	}
}
