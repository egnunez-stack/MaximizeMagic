package com.gen.maximizemagic.model

import android.content.Intent
import android.provider.AlarmClock
import com.gen.maximizemagic.MainActivity
import java.util.Calendar

actual class AlarmManager actual constructor() {
    actual fun setAlarm(timeMillis: Long, message: String) {
        val activity = MainActivity.currentActivity ?: return

        val calendar = Calendar.getInstance().apply {
            this.timeInMillis = timeMillis
        }

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            // Esto evita que se abra la app de reloj y te interrumpa
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }

        // Verificamos que exista una app de reloj que pueda manejar esto
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
            println("#MaximizeMagic: Alarma programada para las $hour:$minute")
        } else {
            // Si falla el modo silencioso, intentamos el modo normal
            intent.removeExtra(AlarmClock.EXTRA_SKIP_UI)
            activity.startActivity(intent)
        }
    }
}