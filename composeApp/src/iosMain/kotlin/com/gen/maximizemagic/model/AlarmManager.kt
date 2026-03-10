package com.gen.maximizemagic.model

actual class AlarmManager actual constructor() {
    actual fun setAlarm(timeMillis: Long, message: String) {
        println("iOS: AlarmManager.setAlarm not implemented")
    }
}
