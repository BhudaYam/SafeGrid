package com.example.safegrid

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safegrid.ui.DurbanMapScreen
import com.example.safegrid.ui.HomeScreen
import com.example.safegrid.ui.LoginScreen
import com.example.safegrid.ui.PowerScreen
import com.example.safegrid.ui.RegisterScreen
import com.example.safegrid.ui.ReportScreen
import com.example.safegrid.ui.S
import com.example.safegrid.ui.SafeGridViewModel
import com.example.safegrid.ui.Screen
import com.example.safegrid.ui.SettingsScreen
import com.example.safegrid.ui.SosScreen
import com.example.safegrid.ui.SplashScreen
import com.example.safegrid.ui.Strings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SafeGridViewModel = viewModel()
            SafeGridApp(viewModel)
        }
    }
}

@Composable
fun SafeGridApp(viewModel: SafeGridViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val activity = LocalContext.current as ComponentActivity
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.onPhotoPicked(uri)
    }
    val notifications = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    val location = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    val dark = isSystemInDarkTheme()

    MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
        when (state.screen) {
            Screen.SPLASH -> SplashScreen(onGetStarted = { viewModel.continueFromSplash() })
            Screen.LOGIN -> LoginScreen(
                state = state,
                onLogin = viewModel::login,
                onGoogle = { viewModel.loginWithGoogle(activity) },
                onRegister = { viewModel.open(Screen.REGISTER) }
            )
            Screen.REGISTER -> RegisterScreen(
                state = state,
                onRegister = viewModel::register,
                onGoogle = { viewModel.loginWithGoogle(activity) },
                onBack = { viewModel.open(Screen.LOGIN) }
            )
            else -> {
                Scaffold(
                    bottomBar = {
                        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                            NavItem(state.screen == Screen.HOME, "⌂", Strings.get(state.lang, S.Home)) {
                                viewModel.open(Screen.HOME)
                            }
                            NavItem(state.screen == Screen.POWER, "⚡", Strings.get(state.lang, S.Power)) {
                                viewModel.open(Screen.POWER)
                            }
                            NavItem(state.screen == Screen.MAP || state.screen == Screen.REPORT, "📍", Strings.get(state.lang, S.Map)) {
                                viewModel.open(Screen.MAP)
                            }
                            NavItem(state.screen == Screen.SOS, "🚨", Strings.get(state.lang, S.Sos)) {
                                viewModel.open(Screen.SOS)
                            }
                            NavItem(state.screen == Screen.SETTINGS, "⚙", Strings.get(state.lang, S.Settings)) {
                                viewModel.open(Screen.SETTINGS)
                            }
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                        when (state.screen) {
                            Screen.POWER -> PowerScreen(state, viewModel)
                            Screen.MAP -> DurbanMapScreen(
                                onReport = {
                                    location.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                    viewModel.open(Screen.REPORT)
                                }
                            )
                            Screen.REPORT -> ReportScreen(
                                state = state,
                                onBack = { viewModel.open(Screen.MAP) },
                                onPickPhoto = { photoPicker.launch("image/*") },
                                onSubmit = viewModel::submitReport
                            )
                            Screen.SOS -> SosScreen(
                                state = state,
                                onActivate = {
                                    location.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                    viewModel.activateSos()
                                },
                                onCancel = { viewModel.cancelSos() }
                            )
                            Screen.SETTINGS -> SettingsScreen(
                                state = state,
                                viewModel = viewModel,
                                onRequestNotifications = {
                                    if (Build.VERSION.SDK_INT >= 33) {
                                        notifications.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            )
                            else -> HomeScreen(
                                state = state,
                                viewModel = viewModel,
                                onPower = { viewModel.open(Screen.POWER) },
                                onMap = { viewModel.open(Screen.MAP) },
                                onReport = { viewModel.open(Screen.REPORT) },
                                onSos = { viewModel.open(Screen.SOS) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.NavItem(selected: Boolean, icon: String, label: String, onClick: () -> Unit) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Text(icon, fontSize = 18.sp) },
        label = { Text(label) }
    )
}