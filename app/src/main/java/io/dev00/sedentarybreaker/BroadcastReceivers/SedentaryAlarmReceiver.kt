package io.dev00.sedentarybreaker.BroadcastReceivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import io.dev00.sedentarybreaker.R
import io.dev00.sedentarybreaker.TAG
import io.dev00.sedentarybreaker.data.SedentaryBreakerDatabase
import java.text.SimpleDateFormat
import java.util.*

class SedentaryAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //Send notification for each hour
        val db = Room.databaseBuilder(
            context,
            SedentaryBreakerDatabase::class.java,
            "sedBreaker_db"
        ).build()
        val dao = db.sedentaryBreakerDAO()
        val currentTime = SimpleDateFormat("HH").format(Date()).toInt()
        val thread = Thread {
            //if cleared stop thread
            while (true) {
                val alarmIsSet = dao.getAlarmIsSet()
                if (alarmIsSet != null && alarmIsSet.isSet && (currentTime in 9..20)) {
                    generateNotification(
                        "Sedentary Trigger",
                        "You have been sitting for a long period. Take a hike",
                        NotificationManager.IMPORTANCE_HIGH,
                        10000,
                        context,
                        intent
                    )
                } else {
                    break
                }
                Thread.sleep(1000 * 3600)
            }
        }
        thread.start()

    }

    fun generateNotification(
        text: String,
        title: String,
        priority: Int,
        id: Int,
        context: Context,
        intent: Intent?
    ) {
        try {
            val flags = when {
                true -> PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                else -> PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, flags)
            val builder = NotificationCompat.Builder(
                context,
                text
            )
            builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(text)
                .setContentTitle(title)
                .setPriority(priority)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            val notificationManager =
                ContextCompat.getSystemService(context, NotificationManager::class.java)

            val channel = NotificationChannel(
                title,
                text,
                priority
            )
            notificationManager?.createNotificationChannel(channel)
            builder.setChannelId(title)

            notificationManager?.notify(id, builder.build())

        } catch (e: Exception) {
            Log.e("Weather", e.toString())
        }

    }
}