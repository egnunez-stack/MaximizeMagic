package com.gen.maximizemagic.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.gen.maximizemagic.MainActivity

actual class AlarmManager actual constructor() {
    actual fun setAlarm(timeMillis: Long, message: String) {
        val activity = MainActivity.currentActivity ?: return
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Verificar permisos para Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                activity.startActivity(intent)
                return
            }
        }

        // Usamos nuestro propio AlarmReceiver en lugar de la app de Reloj
        val intent = Intent(activity, AlarmReceiver::class.java).apply {
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            activity,
            timeMillis.toInt(), // ID único
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Programar el milisegundo exacto (Independiente del día)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent)
        }

        println("#MaximizeMagic: Alarma registrada en el sistema para milisegundos: $timeMillis")
    }
}