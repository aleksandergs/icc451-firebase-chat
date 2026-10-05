package pucmm.args.icc451_firebase_chat.utils

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


internal object TimeUtils {

	fun formatTimestamp(timestamp: Long): String {
		// Zona horaria local del dispositivo
		val zoneId = ZoneId.systemDefault()
		val now = ZonedDateTime.now(zoneId)
		// Convertir el timestamp a la zona horaria local
		val messageTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId)

		val pattern = when {
			// mismo dia -> solo la hora
			now.toLocalDate() == messageTime.toLocalDate() -> "HH:mm"
			// ayer -> 'Ayer' + hora
			now.minusDays(1).toLocalDate() == messageTime.toLocalDate() -> "Ayer HH:mm"
			// menos de una semana -> dia de la semana + hora
			ChronoUnit.DAYS.between(messageTime.toLocalDate(), now.toLocalDate()) < 7 -> "EEEE HH:mm"
			// mismo año -> dia y mes + hora
			now.year == messageTime.year -> "dd/MM HH:mm"
			// otro año -> dia, mes y año + hora
			else -> "dd/MM/yyyy HH:mm"
		}
		return messageTime.format(DateTimeFormatter.ofPattern(pattern))
	}
}
