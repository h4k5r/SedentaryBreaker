package io.dev00.sedentarybreaker.DataSources

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.tasks.CancellationTokenSource
import java.lang.Exception
import com.google.android.gms.location.FusedLocationProviderClient


@SuppressLint("MissingPermission")
fun getLocation(
    client: FusedLocationProviderClient,
    onSuccessListener: (latitude: Double, longitude: Double) -> Unit = { _: Double, _: Double -> },
    onFailureListener: (exception: Exception?) -> Unit = {}
) {
    val location = client.getCurrentLocation(100, CancellationTokenSource().token)
    location.addOnCompleteListener {
        if (it.isSuccessful) {
            onSuccessListener(it.result.latitude, it.result.longitude);
        } else {
            onFailureListener(it.exception)
        }
    }
}