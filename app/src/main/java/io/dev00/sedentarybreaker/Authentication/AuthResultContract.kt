package io.dev00.sedentarybreaker.Authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.tasks.Task

class AuthResultContract : ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {

    override fun createIntent(context: Context, input: Int): Intent =
        getGoogleSignInClient(context).signInIntent.putExtra("input", input)

    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
        return when (resultCode) {
            Activity.RESULT_OK -> GoogleSignIn.getSignedInAccountFromIntent(intent)
            else -> null
        }
    }

}

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_CADENCE, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
        .build();
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Fitness.SCOPE_ACTIVITY_READ_WRITE)
        .requestScopes(Fitness.SCOPE_BODY_READ_WRITE)
        .requestScopes(Fitness.SCOPE_NUTRITION_READ_WRITE)
        .requestScopes(Fitness.SCOPE_LOCATION_READ_WRITE)
        .addExtension(fitnessOptions)
        .build()
    return GoogleSignIn.getClient(context, signInOptions)
}