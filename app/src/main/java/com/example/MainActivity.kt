package com.example

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.MyApplicationTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Smart5GOnlyApp(onNavigateToPrivacy = { navController.navigate("privacy") })
        }
        composable("privacy") {
            PrivacyPolicyScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Smart5GOnlyApp(
    viewModel: SignalViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToPrivacy: () -> Unit = {}
) {
    val context = LocalContext.current
    
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            viewModel.startPolling(context)
        } else {
            permissionState.launchMultiplePermissionRequest()
        }
    }
    
    val signalHistory by viewModel.signalHistory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Title / Dashboard Header
        Text(
            text = "Smart 5G Only",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        SignalStrengthChart(signalData = signalHistory)

        Spacer(modifier = Modifier.height(24.dp))

        // Prominent Button
        Button(
            onClick = {
                // Method 1: Intent with class "com.android.settings.RadioInfo"
                val tryMethod1 = fun(): Boolean {
                    return try {
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.setClassName("com.android.settings", "com.android.settings.RadioInfo")
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                        true
                    } catch (e: Exception) {
                        false
                    }
                }

                // Method 2: Intent with action "android.intent.action.MAIN" and class "com.android.settings.TestingSettings"
                val tryMethod2 = fun(): Boolean {
                    return try {
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.setClassName("com.android.settings", "com.android.settings.TestingSettings")
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                        true
                    } catch (e: Exception) {
                        false
                    }
                }

                // Method 3: Intent with class "com.android.phone.settings.RadioInfo"
                val tryMethod3 = fun(): Boolean {
                    return try {
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.setClassName("com.android.phone", "com.android.phone.settings.RadioInfo")
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                        true
                    } catch (e: Exception) {
                        false
                    }
                }

                // Execute sequentially
                if (!tryMethod1()) {
                    if (!tryMethod2()) {
                        if (!tryMethod3()) {
                            Toast.makeText(
                                context,
                                "Advanced settings blocked by manufacturer. Unable to open.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Open 5G Settings",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        // Instructions Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Instructions:",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Text(
                    text = "1. Click Open Settings -> 2. Find 'Set Preferred Network Type' -> 3. Select 'NR Only' -> 4. Toggle Flight mode if network doesn't update immediately.",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Ads Beginner Box (Ad Banner Placeholder)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(0xFF2C2C2C), shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ads Beginner Box",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onNavigateToPrivacy) {
            Text(
                text = "Privacy Policy",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SignalStrengthChart(signalData: List<Int>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Real-Time Signal Strength (dBm)",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            val currentDbm = signalData.lastOrNull() ?: -120
            Text(
                text = if (currentDbm <= -120) "Searching..." else "$currentDbm dBm",
                color = if (currentDbm > -90) Color(0xFF3DDC84) else if (currentDbm > -110) Color(0xFFFFC107) else Color(0xFFE53935),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                val minDbm = -120f
                val maxDbm = -50f
                
                val width = size.width
                val height = size.height
                
                val pointWidth = width / (signalData.size - 1).coerceAtLeast(1)
                val path = Path()
                
                signalData.forEachIndexed { index, dbm ->
                    val clamped = dbm.toFloat().coerceIn(minDbm, maxDbm)
                    val normalized = ((clamped - minDbm) / (maxDbm - minDbm))
                    val y = height - (normalized * height)
                    val x = index * pointWidth
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                
                drawPath(
                    path = path,
                    color = Color(0xFF3DDC84),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}

class SignalViewModel : ViewModel() {
    private val maxDataPoints = 40
    private val _signalHistory = MutableStateFlow<List<Int>>(List(maxDataPoints) { -120 })
    val signalHistory: StateFlow<List<Int>> = _signalHistory.asStateFlow()

    private var currentSignalDbm = -120
    private var isPolling = false

    fun startPolling(context: Context) {
        if (isPolling) return
        isPolling = true

        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val callback = object : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
                    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                        extractSignal(signalStrength)
                    }
                }
                telephonyManager.registerTelephonyCallback(context.mainExecutor, callback)
            } else {
                val listener = object : PhoneStateListener() {
                    @Deprecated("Deprecated in Java")
                    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                        extractSignal(signalStrength)
                    }
                }
                telephonyManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
            }
        } catch (e: SecurityException) {
            // Permissions not fully granted or policy block
        }

        viewModelScope.launch {
            while(true) {
                val currentList = _signalHistory.value.toMutableList()
                currentList.add(currentSignalDbm)
                if (currentList.size > maxDataPoints) {
                    currentList.removeAt(0)
                }
                _signalHistory.value = currentList
                delay(1000)
            }
        }
    }

    private fun extractSignal(signalStrength: SignalStrength) {
        var dbm = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val cellSignals = signalStrength.cellSignalStrengths
            if (cellSignals.isNotEmpty()) {
                dbm = cellSignals[0].dbm
            }
        }
        
        if (dbm == 0 || dbm == Int.MAX_VALUE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val level = signalStrength.level // 0 to 4
                dbm = -120 + (level * 15) // Approximation
            } else {
                dbm = if (signalStrength.isGsm) {
                    val asu = signalStrength.gsmSignalStrength
                    if (asu != 99) -113 + 2 * asu else -120
                } else {
                    signalStrength.cdmaDbm
                }
            }
        }
        
        if (dbm != Int.MAX_VALUE && dbm < 0) {
            currentSignalDbm = dbm
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E1E)
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Privacy Policy",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = """
                    Last updated: September 15, 2026
                    
                    1. Information Collection
                    This application requires access to your device's Phone State and Location permissions to accurately measure and display real-time cellular signal strength (dBm) and network types.
                    
                    2. Use of Information
                    The information collected is used solely within the application to provide you with real-time feedback on your cellular connection. We do not transmit, store remotely, or share this data with any third parties. 
                    
                    3. Third-Party Services
                    This app may contain placeholders or actual implementations of third-party advertisements. These third-party ad networks may collect anonymized data as per their own privacy policies to serve relevant ads.
                    
                    4. Changes to This Policy
                    We may update this Privacy Policy from time to time. We will notify you of any changes by updating the new Privacy Policy on this page.
                    
                    5. Contact Us
                    If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact us.
                """.trimIndent(),
                color = Color.LightGray,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}


