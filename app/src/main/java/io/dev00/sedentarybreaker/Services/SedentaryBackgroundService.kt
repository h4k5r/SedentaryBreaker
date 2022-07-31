package io.dev00.sedentarybreaker.Services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.FusedLocationProviderClient
import io.dev00.sedentarybreaker.BroadcastReceivers.ActivityTransitionReceiver
import io.dev00.sedentarybreaker.DataSources.getLocation
import io.dev00.sedentarybreaker.R
import io.dev00.sedentarybreaker.Utils.ActivityTransitionsUtil
import io.dev00.sedentarybreaker.Utils.Constants
import io.dev00.sedentarybreaker.Utils.Utils
import io.dev00.sedentarybreaker.data.SedentaryBreakerDatabase
import java.text.SimpleDateFormat
import java.util.*


class SedentaryBackgroundService  : Service() {
    lateinit var client: ActivityRecognitionClient
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        client = ActivityRecognition.getClient(this)
//        requestForUpdates()
        val db = Room.databaseBuilder(
            this,
            SedentaryBreakerDatabase::class.java,
            "sedBreaker_db"
        ).build()
        val dao = db.sedentaryBreakerDAO()
        val client = FusedLocationProviderClient(this)

        val t = Thread {
            while (true) {
                val currentTime = SimpleDateFormat("HH").format(Date()).toInt()
                val currentHomeLocation = dao.getHomeLocation()
                if ( (currentTime in 9..20) && currentHomeLocation != null) {
                    getLocation(client = client, onSuccessListener = { latitude, longitude ->
                        if (
                            Utils.isWithinRange(
                                homeLatitude = currentHomeLocation.lat,
                                homeLongitude = currentHomeLocation.lon,
                                currentLat = latitude,
                                currentLong = longitude
                            )
                        ) {
                            Utils.getCustomNotification( context = this) {
                                generateNotification(
                                    it,
                                    "Sedentary Trigger",
                                    NotificationManager.IMPORTANCE_HIGH,
                                    10000,
                                    this,
                                    intent
                                )
                            }
                        }
                    })
                } else {
                    Toast.makeText(this,"Home Location Set",Toast.LENGTH_SHORT).show()
                    break
                }
                Thread.sleep(1000 * 3600)
            }
        }
        t.start()

        val CHANNEL_ID = "Sedentary Service"
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_ID,
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            notificationChannel
        )
        val notification =
            Notification.Builder(this, CHANNEL_ID)
                .setContentText("Service is Running")
                .setContentTitle("Sedentary Breaker").build()
        startForeground(1002, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        deregisterForUpdates()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    private fun requestForUpdates() {
        client
            .requestActivityTransitionUpdates(
                ActivityTransitionsUtil.getActivityTransitionRequest(),
                getPendingIntent()
            )
            .addOnSuccessListener {
                Log.d("TAG", "onReceive: Registered")
                showToast("successful registration")
            }
            .addOnFailureListener { e: Exception ->
                showToast("Unsuccessful registration")
            }
    }

    private fun deregisterForUpdates() {
        client
            .removeActivityTransitionUpdates(getPendingIntent())
            .addOnSuccessListener {
                getPendingIntent().cancel()
                showToast("successful deregistration")
            }
            .addOnFailureListener { e: Exception ->
                showToast("unsuccessful deregistration")
            }
    }
    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, ActivityTransitionReceiver::class.java)
        return PendingIntent.getBroadcast(
            this,
            Constants.REQUEST_CODE_INTENT_ACTIVITY_TRANSITION,
            intent,
            PendingIntent.FLAG_MUTABLE
        )
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG)
            .show()
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