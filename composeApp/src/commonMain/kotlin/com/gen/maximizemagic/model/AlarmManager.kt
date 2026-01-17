package com.gen.maximizemagic.model

expect class AlarmManager() {
    /**
     * @param timeMillis Momento exacto de la alarma en milisegundos
     * @param message Mensaje de la alarma
     */
    fun setAlarm(timeMillis: Long, message: String)
}
