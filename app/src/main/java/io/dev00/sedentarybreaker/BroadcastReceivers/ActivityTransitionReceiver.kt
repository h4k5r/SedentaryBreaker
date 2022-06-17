package io.dev00.sedentarybreaker.BroadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.room.Room
import com.google.android.gms.location.ActivityTransitionResult
import io.dev00.sedentarybreaker.Triggers.SedentaryTrigger
import io.dev00.sedentarybreaker.Utils.ActivityTransitionsUtil
import io.dev00.sedentarybreaker.data.SedentaryBreakerDatabase
import io.dev00.sedentarybreaker.models.UserActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class ActivityTransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            result?.let {
                result.transitionEvents.forEach { event ->
                    val eventType = ActivityTransitionsUtil.toActivityString(event.activityType)
                    val transitionType =
                        ActivityTransitionsUtil.toTransitionType(event.transitionType)
                    val info =
                        "Transition: " + ActivityTransitionsUtil.toActivityString(event.activityType) +
                                " (" + ActivityTransitionsUtil.toTransitionType(event.transitionType) + ")" + "   " +
                                SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
                    val db = Room.databaseBuilder(
                        context,
                        SedentaryBreakerDatabase::class.java,
                        "sedBreaker_db"
                    ).build()
                    val dao = db.sedentaryBreakerDAO()
                    GlobalScope.launch {
                        val userActivity = dao.getUserActivity()
                        if (userActivity != null) {
                            userActivity.activityState = transitionType
                            userActivity.detectedActivity = eventType
                            userActivity.time = System.currentTimeMillis().toString()
                            dao.updateUserActivity(userActivity)
                        } else {
                            dao.insertUserActivity(
                                UserActivity(
                                    activityState = transitionType,
                                    detectedActivity = eventType,
                                    time = System.currentTimeMillis().toString()
                                )
                            )
                        }
                    }

                    SedentaryTrigger.trigger(
                        eventType = eventType,
                        transitionType = transitionType,
                        context = context
                    )
                    Toast.makeText(context, info, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}