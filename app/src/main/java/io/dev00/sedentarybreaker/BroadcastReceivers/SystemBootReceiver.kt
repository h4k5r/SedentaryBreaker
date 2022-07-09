package io.dev00.sedentarybreaker.BroadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.dev00.sedentarybreaker.ServicesManager
import io.dev00.sedentarybreaker.Utils.Utils.services

class SystemBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            ServicesManager.startServices(context, services)
        }
    }
}