package io.dev00.sedentarybreaker.Utils

import android.location.Location

object Utils {
    fun isWithinRange(
        homeLongitude: Double,
        homeLatitude: Double,
        currentLat: Double,
        currentLong: Double
    ): Boolean {
        val dist = FloatArray(1)
        Location.distanceBetween(homeLatitude, homeLongitude, currentLat, currentLong, dist)
        return dist[0] < 150
    }
}