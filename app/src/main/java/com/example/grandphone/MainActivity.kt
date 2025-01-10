package com.example.grandphone

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.grandphone.ui.theme.GrandphoneTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrandphoneTheme {
                MainScreen(
                    getBatteryStatus = { getBatteryLevel() },
                    getSignalStrength = { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        getSignalStrength()
                    } else {
                        TODO("VERSION.SDK_INT < P")
                    }
                    }
                )
            }
        }
    }

    private fun getBatteryLevel(): String {
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return "$batteryLevel%"
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun getSignalStrength(): String {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val signalStrength = telephonyManager.signalStrength
        return if (signalStrength != null) {
            "${signalStrength.level * -20} dBm"
        } else {
            "No Signal"
        }
    }
}

@Composable
fun MainScreen(
    getBatteryStatus: () -> String,
    getSignalStrength: () -> String
) {
    var batteryStatus by remember { mutableStateOf("Battery: --%") }
    var signalStatus by remember { mutableStateOf("Signal: -- dBm") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            while (true) {
                batteryStatus = "Battery: ${getBatteryStatus()}"
                signalStatus = "Signal: ${getSignalStrength()}"
                delay(1000)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        StatusBar(battery = batteryStatus, signal = signalStatus)
        ClockDisplay()
        ButtonGrid()
    }
}

@Composable
fun StatusBar(battery: String, signal: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF4F4F4))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = battery, fontSize = 16.sp)
        Text(text = signal, fontSize = 16.sp)
    }
}

@Composable
fun ClockDisplay() {
    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            delay(1000)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        color = Color(0xFFE74C3C)
    ) {
        Text(
            text = currentTime,
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
            fontSize = 48.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ButtonGrid() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton("Call", R.drawable.phone, context)
            ActionButton("Message", R.drawable.communication, context)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Space Evenly
        ) {
            ActionButton("Email", R.drawable.email, context)
            ActionButton("Settings", R.drawable.settings, context)
        }
    }
}

@Composable
fun ActionButton(label: String, iconRes: Int, context: Context) {
    Button(onClick = {
        when (label) {
            "Call" -> {
                val intent = Intent(Intent.ACTION_DIAL)
                context.startActivity(intent)
            }
            "Message" -> {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:")
                }
                context.startActivity(intent)
            }
            "Email" -> {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                }
                context.startActivity(intent)
            }
            "Settings" -> {
                val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
                context.startActivity(intent)
            }
        }
    }) {
        Image(painter = painterResource(id = iconRes), contentDescription = label)
        Text(text = label)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GrandphoneTheme {
        MainScreen(
            getBatteryStatus = { "Battery: 50%" },
            getSignalStrength = { "Signal: -70 dBm" }
        )
    }
}