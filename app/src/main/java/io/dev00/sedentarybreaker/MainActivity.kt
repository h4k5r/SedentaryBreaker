package io.dev00.sedentarybreaker

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import io.dev00.sedentarybreaker.Authentication.AuthResultContract
import io.dev00.sedentarybreaker.DataSources.getLocation
import io.dev00.sedentarybreaker.ui.theme.SedentaryBreakerTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

val TAG = "TAG"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityRecognitionClient = ActivityRecognition.getClient(this)
        Log.d(TAG, "onCreate:  \n")
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
            verticalArrangement = Arrangement.SpaceAround,
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
                Text(text = "Sedentary Breaker")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Set Current Location as Home")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    MapsTest()
                }
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

@Composable
fun MapsTest() {
    var initialRun by remember {
        mutableStateOf(true)
    }
    var markerPosition by remember {
        mutableStateOf(LatLng(0.0, 0.0))
    }
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 15f)
    }
    if (initialRun) {
        getLocation(
            FusedLocationProviderClient(context),
            onSuccessListener = { latitude, longitude ->
                markerPosition = LatLng(latitude, longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(markerPosition, 15f)
            },
            onFailureListener = {
                Log.e(TAG, "MapsTest: ${it.toString()}")
            })
        initialRun = false
    }

    Card(shape = RoundedCornerShape(5)) {
        GoogleMap(
            modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                position = markerPosition,
                title = "Your Title",
                snippet = "Place Name"
            )
        }
    }
}