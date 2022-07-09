package io.dev00.sedentarybreaker.Utils

import android.content.Context
import android.location.Location
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.dev00.sedentarybreaker.DataSources.getCalories
import io.dev00.sedentarybreaker.DataSources.getFoodNutrition
import io.dev00.sedentarybreaker.DataSources.getGoal
import io.dev00.sedentarybreaker.DataSources.getTodayTotalSteps
import io.dev00.sedentarybreaker.Services.SedentaryBackgroundService
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

    fun getCustomNotification(weatherInfo: String, context: Context): String {
        val random = (1..3).random()
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
                        }
                    }
                }
                2 -> {
                    notificationText.append("The Weather is $weatherInfo, and you could go for a walk")
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
                    }
                }
            }
        }
        return notificationText.toString()
    }
}