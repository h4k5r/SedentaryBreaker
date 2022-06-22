package io.dev00.sedentarybreaker.Triggers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.room.Room
import io.dev00.sedentarybreaker.BroadcastReceivers.SedentaryAlarmReceiver
import io.dev00.sedentarybreaker.data.SedentaryBreakerDatabase
import io.dev00.sedentarybreaker.models.AlarmIsSet
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object SedentaryTrigger {
    fun trigger(eventType: String, transitionType: String, context: Context) {

        when (eventType) {
            "STILL" -> {
                if (transitionType == "ENTER") {
                    setAlarm(context)
                }
            }
            "WALKING" -> {
                if (transitionType == "ENTER") {
                    clearAlarm(context)
                }
            }
            "IN VEHICLE" -> {
                if (transitionType == "ENTER") {
                    setAlarm(context)
                }
            }
            "RUNNING" -> {
                if (transitionType == "ENTER") {
                    clearAlarm(context)
                }
            }
            "ON_BICYCLE" -> {
                if (transitionType == "ENTER") {
                    clearAlarm(context)
                }
            }
            else -> {

            }
        }
    }

    private fun getPendingIndent(context: Context): PendingIntent? {
        val intent = Intent(context, SedentaryAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(context, 192837, intent, PendingIntent.FLAG_MUTABLE)
    }

    fun setAlarm(context: Context) {

        val db = Room.databaseBuilder(
            context,
            SedentaryBreakerDatabase::class.java,
            "sedBreaker_db"
        ).build()
        val dao = db.sedentaryBreakerDAO()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            1000 * 5,
            getPendingIndent(context)
        );

        val alarmIsSet = dao.getAlarmIsSet()
        GlobalScope.launch {
            if (alarmIsSet == null) {
                dao.insertAlarmIsSet(AlarmIsSet(isSet = true))
            } else {
                alarmIsSet.isSet = true
                dao.updateAlarmIsSet(alarmIsSet)
            }
        }
    }

    private fun clearAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmManager.cancel(getPendingIndent(context))

        val db = Room.databaseBuilder(
            context,
            SedentaryBreakerDatabase::class.java,
            "sedBreaker_db"
        ).build()
        val dao = db.sedentaryBreakerDAO()

        val alarmIsSet = dao.getAlarmIsSet()
        if (alarmIsSet != null) {
            alarmIsSet.isSet = false
            GlobalScope.launch {
                dao.updateAlarmIsSet(alarmIsSet)
            }
        }
    }
}