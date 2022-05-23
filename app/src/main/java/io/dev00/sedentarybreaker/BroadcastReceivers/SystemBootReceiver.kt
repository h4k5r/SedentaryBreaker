package io.dev00.sedentarybreaker.BroadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.dev00.sedentarybreaker.Services.SedentaryBackgroundService
import io.dev00.sedentarybreaker.ServicesManager

class SystemBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val services = listOf(
            SedentaryBackgroundService::class.java,
            //add services here
        )
        if (context != null) {
            ServicesManager.startServices(context, services)
        }
    }
}