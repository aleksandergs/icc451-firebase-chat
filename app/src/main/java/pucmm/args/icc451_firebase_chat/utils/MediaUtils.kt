package pucmm.args.icc451_firebase_chat.utils

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pucmm.args.icc451_firebase_chat.R
import java.io.File
import java.net.URL
import androidx.core.net.toUri

internal object MediaUtils {

	// Subcarpeta dentro del álbum, para que quede como <álbum>/media
	private const val MEDIA_FOLDER = "media"

	// Guarda la imagen en el álbum de la app dentro de la galería del dispositivo
	suspend fun saveImage(context: Context, imageUrl: String): Boolean = withContext(Dispatchers.IO) {
		val album = context.getString(R.string.media_album_name)
		try {
			val bytes = readImage(context, imageUrl) ?: return@withContext false
			val mimeType = imgType(context, imageUrl)
			val fileName = "${album}_${System.currentTimeMillis()}.${extensionOf(mimeType)}"

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				saveWithMediaStore(context, album, bytes, fileName, mimeType)
			} else {
				saveToPicturesFolder(context, album, bytes, fileName)
			}
		} catch (e: Exception) {
			false
		}
	}

	private fun readImage(context: Context, imageUrl: String): ByteArray? = when {
		imageUrl.startsWith("http") -> URL(imageUrl).openStream().use { it.readBytes() }
		else -> context.contentResolver.openInputStream(imageUrl.toUri())?.use { it.readBytes() }
	}

	// Api 29 en adelante escribe en la galería sin permisos
	@RequiresApi(Build.VERSION_CODES.Q)
	private fun saveWithMediaStore(
		context: Context,
		album: String,
		bytes: ByteArray,
		fileName: String,
		mimeType: String,
	): Boolean {
		val resolver = context.contentResolver
		val values = ContentValues().apply {
			put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
			put(MediaStore.Images.Media.MIME_TYPE, mimeType)
			put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$album/$MEDIA_FOLDER")
			// Mientras se escribe, la imagen no es visible para otras apps
			put(MediaStore.Images.Media.IS_PENDING, 1)
		}

		val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false
		resolver.openOutputStream(uri)?.use { it.write(bytes) }

		values.clear()
		values.put(MediaStore.Images.Media.IS_PENDING, 0)
		resolver.update(uri, values, null, null)
		return true
	}

	// Api 28 y anteriores escriben el archivo directamente (necesita WRITE_EXTERNAL_STORAGE)
	@Suppress("DEPRECATION")
	private fun saveToPicturesFolder(context: Context, album: String, bytes: ByteArray, fileName: String): Boolean {
		val folder = File(
			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
			"$album/$MEDIA_FOLDER",
		)
		if (!folder.exists() && !folder.mkdirs())
			return false

		val file = File(folder, fileName)
		file.outputStream().use { it.write(bytes) }
		// Avisa a la galería para que el álbum aparezca sin reiniciar el dispositivo
		MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
		return true
	}

	private fun imgType(context: Context, imageUrl: String): String {
		if (!imageUrl.startsWith("http")) {
			context.contentResolver.getType(imageUrl.toUri())?.let { return it }
		}
		return when {
			imageUrl.endsWith(".png", true) -> "image/png"
			imageUrl.endsWith(".webp", true) -> "image/webp"
			else -> "image/jpeg"
		}
	}

	private fun extensionOf(mimeType: String): String = when (mimeType) {
		"image/png" -> "png"
		"image/webp" -> "webp"
		else -> "jpg"
	}
}
