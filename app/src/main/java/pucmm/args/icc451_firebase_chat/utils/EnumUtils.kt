package pucmm.args.icc451_firebase_chat.utils

import pucmm.args.icc451_firebase_chat.data.model.MessageType

internal object EnumUtils {

	fun messageType(text: String, imageURL: String?): MessageType {
		return when {
			text.isNotBlank() && imageURL != null -> MessageType.IMAGE_TEXT
			text.isNotBlank() -> MessageType.TEXT
			imageURL != null -> MessageType.IMAGE
			else -> throw IllegalArgumentException("Los mensajes deben tener texto o imagen")
		}
	}
}