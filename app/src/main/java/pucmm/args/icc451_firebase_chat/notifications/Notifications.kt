package pucmm.args.icc451_firebase_chat.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import pucmm.args.icc451_firebase_chat.R
import pucmm.args.icc451_firebase_chat.ui.chat.ChatActivity
import pucmm.args.icc451_firebase_chat.ui.main.MainActivity

internal object Notifications {

	private const val CHANNEL_ID = "messages"

	fun createChannel(context: Context) {
		val channel = NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_channel_name),
			NotificationManager.IMPORTANCE_HIGH,)
		context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
	}

	// Muestra la notificación de un mensaje nuevo. Al tocarla se abre el chat
	fun showMessage(context: Context, title: String, body: String, otherUserId: String) {
		// Una notificación por chat, así no se reemplazan entre sí
		val notificationId = otherUserId.hashCode()

		val intent = if (otherUserId.isNotBlank()) {
			Intent(context, ChatActivity::class.java).apply {
				putExtra(ChatActivity.EXTRA_OTHER_USER_ID, otherUserId)
			}
		} else {
			Intent(context, MainActivity::class.java)
		}

		// El mismo id en el PendingIntent para que cada notificación abra su propio chat
		val pendingIntent = PendingIntent.getActivity(context, notificationId, intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
		)

		val notification = NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.ic_chats)
			.setContentTitle(title).setContentText(body).setAutoCancel(true).setContentIntent(pendingIntent).build()

		// En Android 13 en adelante, si el usuario no dio permiso no se muestra nada
		if (!canNotify(context))
			return

		try {
			NotificationManagerCompat.from(context).notify(notificationId, notification)
		} catch (e: SecurityException) { return }
	}

	// Al abrir el chat se quita su notificación para que no quede pendiente
	fun cancel(context: Context, otherUserId: String) {
		NotificationManagerCompat.from(context).cancel(otherUserId.hashCode())
	}

	private fun canNotify(context: Context): Boolean {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
		return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
	}
}
