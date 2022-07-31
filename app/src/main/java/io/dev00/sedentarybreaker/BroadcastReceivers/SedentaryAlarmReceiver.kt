package io.dev00.sedentarybreaker.BroadcastReceivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import io.dev00.sedentarybreaker.DataSources.getLocation
import io.dev00.sedentarybreaker.R
import io.dev00.sedentarybreaker.Utils.Utils
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
        val client = FusedLocationProviderClient(context)
        val thread = Thread {
            //if cleared stop thread
            while (true) {
                Thread.sleep(
                    1000
//                            * 3600
                )
                val alarmIsSet = dao.getAlarmIsSet()
                val currentHomeLocation = dao.getHomeLocation()
                if (alarmIsSet != null && alarmIsSet.isSet && (currentTime in 9..20) && currentHomeLocation != null) {
                    getLocation(client = client, onSuccessListener = { latitude, longitude ->
                        if (
                            Utils.isWithinRange(
                                homeLatitude = currentHomeLocation.lat,
                                homeLongitude = currentHomeLocation.lon,
                                currentLat = latitude,
                                currentLong = longitude
                            )
                        ) {
                            Utils.getCustomNotification( context = context) {
                                generateNotification(
                                    it,
                                    "Sedentary Trigger",
                                    NotificationManager.IMPORTANCE_HIGH,
                                    10000,
                                    context,
                                    intent
                                )
                            }

                        }
                    })
                } else {
                    Toast.makeText(context,"Home Location Set",Toast.LENGTH_SHORT).show()
                    break
                }
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