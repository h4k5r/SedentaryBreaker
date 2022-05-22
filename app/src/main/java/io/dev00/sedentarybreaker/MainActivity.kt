package io.dev00.sedentarybreaker

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import io.dev00.sedentarybreaker.Authentication.AuthResultContract
import io.dev00.sedentarybreaker.ui.theme.SedentaryBreakerTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SedentaryBreakerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    RequestPermissions()
                }
            }
        }
    }
}

@Composable
fun AuthCompose() {
    val context = LocalContext.current
    var text by remember { mutableStateOf<String?>(null) }
    var currentAccount = GoogleSignIn.getLastSignedInAccount(context)
    var isSignedIn by remember {
        mutableStateOf((currentAccount != null) && !currentAccount.isExpired)
    }
    val coroutineScope = rememberCoroutineScope()
    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) {
            try {
                val account = it?.getResult(ApiException::class.java)
                if (account == null) {
                    text = "Google sign in failed"
                } else {
                    isSignedIn = true
                    Log.d("TAG", "Greeting: ${account.requestedScopes}")
                    coroutineScope.launch {

                    }
                }
            } catch (e: ApiException) {
                text = "Google sign in failed"
            }
        }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isSignedIn) {
                Button(onClick = {
                    authResultLauncher.launch(1)
                }) {
                    Text(text = "Sign in")
                }
            } else {
                //Signed In
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions() {
    val permissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.INTERNET,
        )
    )
    val activityRecognitionPermissions = rememberMultiplePermissionsState(
        listOf(
//            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
        )
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {

        if (permissions.allPermissionsGranted && activityRecognitionPermissions.allPermissionsGranted) {
            AuthCompose()
        } else if (!permissions.allPermissionsGranted) {
            Button(onClick = { permissions.launchMultiplePermissionRequest() }) {
                Text(text = "Request Permissions")
            }
        } else {
            Text(text = "All permissions not Granted, Check denied permissions")

        }
    }
}