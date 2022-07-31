package io.dev00.sedentarybreaker.Utils

import android.content.Context
import android.content.res.Resources
import android.location.Location
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.location.FusedLocationProviderClient
import io.dev00.sedentarybreaker.DataSources.*
import io.dev00.sedentarybreaker.R
import io.dev00.sedentarybreaker.Services.SedentaryBackgroundService
import org.json.JSONObject
import java.time.LocalDate
import java.time.ZoneOffset

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

    val services = listOf(
        SedentaryBackgroundService::class.java,
        //add services here
    )

    fun getCustomNotification(context: Context,onStringConstructed:(constructedString:String) -> Unit) {
        val random = 3
//            (1..3).random()
        val account = GoogleSignIn.getLastSignedInAccount(context)
        val notificationText = StringBuilder()
        if (account != null) {
            when (random) {
                1 -> {
                    getTodayTotalSteps(context = context, account = account) { walkedSteps ->
                        getGoal(context = context, account = account) { goalSteps ->
                            val stepsToBeWalked = goalSteps - walkedSteps
                            if (stepsToBeWalked <= 0) {
                                notificationText.append("Great Job you managed to achieve your goal, and you can do More")
                            } else {
                                notificationText.append("If you walk $stepsToBeWalked steps more you would hit your goal")
                            }
                            onStringConstructed(notificationText.toString())
                        }
                    }
                }
                2 -> {
                    getLocation(
                        client = FusedLocationProviderClient(context),
                        onSuccessListener = { latitude, longitude ->
                            getWeather(
                                API_Key = "5dc3dbe89a4589c36200a697dc68e5f1",
                                LAT = latitude,
                                LON = longitude,
                                context = context
                            ) {
                                val weather: JSONObject = it[0] as JSONObject
                                val weatherID = weather.getInt("id")
                                if (weatherID < 800) {
                                    getTodayTotalSteps(
                                        context = context,
                                        account = account
                                    ) { walkedSteps ->
                                        getGoal(context = context, account = account) { goalSteps ->
                                            val stepsToBeWalked = goalSteps - walkedSteps
                                            if (stepsToBeWalked <= 0) {
                                                notificationText.append("Great Job you managed to achieve your goal, and you can do More")
                                            } else {
                                                notificationText.append("If you walk $stepsToBeWalked steps more you would hit your goal")
                                            }
                                            onStringConstructed(notificationText.toString())
                                        }
                                    }
                                } else {
                                    notificationText.append("The Weather is ${weather.get("description")}, and you could go for a walk")
                                    onStringConstructed(notificationText.toString())

                                }
                            }
                        })
                }
                3 -> {
                    val todayStart =
                        LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
                    getCalories(
                        startSeconds = todayStart,
                        endSeconds = System.currentTimeMillis(),
                        context = context,
                        account = account
                    ) {
                        notificationText.append(
                            "Burn few more calories to Have ${
                                getFoodNutrition(
                                    it.toDouble()
                                )
                            }"
                        )
                        onStringConstructed(notificationText.toString())

                    }
                }
            }
        }
        Log.d(TAG, "getCustomNotification: ${notificationText.toString()}")
    }
}