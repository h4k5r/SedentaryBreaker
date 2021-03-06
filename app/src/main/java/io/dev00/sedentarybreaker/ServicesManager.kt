package io.dev00.sedentarybreaker

import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build

object ServicesManager {
    fun startServices(context: Context, services: List<Class<out Service>> = emptyList()) {
        for (service in services) {
            //check service is running, if not run the service
            if (!isServiceRunning(
                    context = context,
                    serviceName = service.javaClass.name
                )
            ) {
                context.startForegroundService(Intent(context, service))
            }
        }
    }
    private fun isServiceRunning(context: Context, serviceName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getRunningServices(Int.MAX_VALUE).forEach {
            if (serviceName.equals(it.service.className)) {
                return true
            }
        }
        return false
    }
}