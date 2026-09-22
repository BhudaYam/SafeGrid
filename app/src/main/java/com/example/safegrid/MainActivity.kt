package com.example.safegrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// SAFEGRID COLOUR PALETTE


val SafeGreen = Color(0xFF0B6E4F)
val SafeAmber = Color(0xFFF2B134)
val SafeRed = Color(0xFFE63946)
val SafeDark = Color(0xFF12372A)
val SafeLight = Color(0xFFF5F7F6)
val SafeGrey = Color(0xFF66736D)
val SafeWhite = Color.White
val SafeBorder = Color(0xFFD7E1DC)



// MAIN ACTIVITY


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SafeGridApp()
        }
    }
}



// SCREEN NAVIGATION


enum class Screen {
    SPLASH,
    LOGIN,
    REGISTER,
    HOME,
    POWER,
    MAP,
    REPORT,
    SOS,
    SETTINGS
}



// SAFEGRID APP


@Composable
fun SafeGridApp() {

    var currentScreen by remember {
        mutableStateOf(Screen.SPLASH)
    }

    MaterialTheme {

        when (currentScreen) {

            Screen.SPLASH -> {

                SplashScreen(
                    onGetStarted = {
                        currentScreen = Screen.LOGIN
                    }
                )
            }

            Screen.LOGIN -> {

                LoginScreen(
                    onLogin = {
                        currentScreen = Screen.HOME
                    },
                    onRegister = {
                        currentScreen = Screen.REGISTER
                    }
                )
            }

            Screen.REGISTER -> {

                RegisterScreen(
                    onRegister = {
                        currentScreen = Screen.HOME
                    },
                    onBack = {
                        currentScreen = Screen.LOGIN
                    }
                )
            }

            else -> {

                MainAppLayout(
                    currentScreen = currentScreen,
                    onScreenChange = {
                        currentScreen = it
                    }
                )
            }
        }
    }
}



// SPLASH SCREEN


@Composable
fun SplashScreen(
    onGetStarted: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SafeGreen
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(125.dp)
                    .clip(CircleShape)
                    .background(SafeWhite),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "🛡️",
                    fontSize = 65.sp
                )
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            Text(
                text = "SAFEGRID",
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Stay Powered.\nStay Safe.\nStay Connected.",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )

            Spacer(
                modifier = Modifier.height(50.dp)
            )

            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SafeAmber
                )
            ) {

                Text(
                    text = "GET STARTED",
                    color = SafeDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}



// LOGIN SCREEN


@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SafeLight)
            .padding(25.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "🛡️",
            fontSize = 55.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = "Welcome Back",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = SafeDark,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Sign in to continue to SafeGrid",
            color = SafeGrey,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text("Email address")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SafeGreen
            ),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text = "LOGIN",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedButton(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text("Continue with Google")
        }

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        TextButton(
            onClick = onRegister,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {

            Text(
                text = "Don't have an account? Register",
                color = SafeGreen
            )
        }
    }
}


// ==========================================================
// REGISTER SCREEN
// ==========================================================

@Composable
fun RegisterScreen(
    onRegister: () -> Unit,
    onBack: () -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SafeLight)
            .padding(25.dp)
    ) {

        TextButton(
            onClick = onBack
        ) {

            Text(
                text = "← Back",
                color = SafeGreen
            )
        }

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Text(
            text = "Create Account",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = SafeDark
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = "Join your community on SafeGrid",
            color = SafeGrey
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = {
                Text("Full name")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text("Email address")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Button(
            onClick = onRegister,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SafeGreen
            ),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text = "CREATE ACCOUNT",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedButton(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Continue with Google")
        }
    }
}



// MAIN APPLICATION LAYOUT


@Composable
fun MainAppLayout(
    currentScreen: Screen,
    onScreenChange: (Screen) -> Unit
) {

    Scaffold(

        bottomBar = {

            NavigationBar(
                containerColor = Color.White
            ) {

                NavigationBarItem(
                    selected = currentScreen == Screen.HOME,
                    onClick = {
                        onScreenChange(Screen.HOME)
                    },
                    icon = {
                        Text("⌂", fontSize = 22.sp)
                    },
                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.POWER,
                    onClick = {
                        onScreenChange(Screen.POWER)
                    },
                    icon = {
                        Text("⚡", fontSize = 20.sp)
                    },
                    label = {
                        Text("Power")
                    }
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.MAP,
                    onClick = {
                        onScreenChange(Screen.MAP)
                    },
                    icon = {
                        Text("📍", fontSize = 20.sp)
                    },
                    label = {
                        Text("Map")
                    }
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.SOS,
                    onClick = {
                        onScreenChange(Screen.SOS)
                    },
                    icon = {
                        Text("🚨", fontSize = 20.sp)
                    },
                    label = {
                        Text("SOS")
                    }
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.SETTINGS,
                    onClick = {
                        onScreenChange(Screen.SETTINGS)
                    },
                    icon = {
                        Text("⚙", fontSize = 20.sp)
                    },
                    label = {
                        Text("Settings")
                    }
                )
            }
        }

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            when (currentScreen) {

                Screen.HOME -> {

                    HomeScreen(
                        onPower = {
                            onScreenChange(Screen.POWER)
                        },
                        onMap = {
                            onScreenChange(Screen.MAP)
                        },
                        onReport = {
                            onScreenChange(Screen.REPORT)
                        },
                        onSOS = {
                            onScreenChange(Screen.SOS)
                        }
                    )
                }

                Screen.POWER -> {

                    PowerScreen()
                }

                Screen.MAP -> {

                    MapScreen(
                        onReport = {
                            onScreenChange(Screen.REPORT)
                        }
                    )
                }

                Screen.REPORT -> {

                    ReportScreen(
                        onBack = {
                            onScreenChange(Screen.MAP)
                        }
                    )
                }

                Screen.SOS -> {

                    SOSScreen()
                }

                Screen.SETTINGS -> {

                    SettingsScreen()
                }

                else -> {

                    HomeScreen(
                        onPower = {},
                        onMap = {},
                        onReport = {},
                        onSOS = {}
                    )
                }
            }
        }
    }
}


// ==========================================================
// HOME DASHBOARD
// ==========================================================

@Composable
fun HomeScreen(
    onPower: () -> Unit,
    onMap: () -> Unit,
    onReport: () -> Unit,
    onSOS: () -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SafeLight)
            .padding(20.dp)
    ) {

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "Good evening,",
                        color = SafeGrey
                    )

                    Text(
                        text = "Kakuhle 👋",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        color = SafeDark
                    )
                }

                Text(
                    text = "🛡️",
                    fontSize = 40.sp
                )
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            SectionTitle(
                text = "POWER STATUS"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SafeGreen
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "⚡",
                            fontSize = 30.sp
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Text(
                            text = "Loadshedding Stage 2",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(15.dp)
                    )

                    Text(
                        text = "NEXT OUTAGE",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "18:00 - 20:30",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Starts in 01:42:18",
                        color = SafeAmber,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(15.dp)
                    )

                    Button(
                        onClick = onPower,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SafeAmber
                        )
                    ) {

                        Text(
                            text = "View Schedule",
                            color = SafeDark
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            SectionTitle(
                text = "COMMUNITY SAFETY"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "📍",
                            fontSize = 28.sp
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Column {

                            Text(
                                text = "3 incidents nearby",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SafeDark
                            )

                            Text(
                                text = "Stay aware of recent activity.",
                                color = SafeGrey
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(15.dp)
                    )

                    Row {

                        Button(
                            onClick = onMap,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SafeGreen
                            )
                        ) {

                            Text("View Map")
                        }

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        OutlinedButton(
                            onClick = onReport
                        ) {

                            Text("Report")
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            SectionTitle(
                text = "EMERGENCY"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = onSOS,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SafeRed
                ),
                shape = RoundedCornerShape(18.dp)
            ) {

                Text(
                    text = "🚨  SOS EMERGENCY",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {

                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "🛡️",
                        fontSize = 35.sp
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Column {

                        Text(
                            text = "Guardian Points",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )

                        Text(
                            text = "120 points • Community Helper",
                            color = SafeGrey
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}


// ==========================================================
// SECTION TITLE
// ==========================================================

@Composable
fun SectionTitle(
    text: String
) {

    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = SafeGrey
    )
}



// POWER SCREEN


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowerScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SafeLight)
    ) {

        TopAppBar(
            title = {
                Text(
                    text = "Loadshedding Schedule",
                    fontWeight = FontWeight.Bold
                )
            }
        )

        LazyColumn(
            modifier = Modifier.padding(20.dp)
        ) {

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SafeGreen
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "YOUR AREA",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )

                        Text(
                            text = "Johannesburg",
                            color = Color.White,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(15.dp)
                        )

                        Text(
                            text = "Current Stage: 2",
                            color = SafeAmber,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {

                        Text(
                            text = "💾 OFFLINE SCHEDULE",
                            color = SafeGreen,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )

                        Text(
                            text = "Showing cached schedule.",
                            color = SafeGrey
                        )

                        Text(
                            text = "Last synced today at 15:20",
                            color = SafeGrey,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                ScheduleItem(
                    day = "Today",
                    time = "18:00 - 20:30"
                )

                ScheduleItem(
                    day = "Tomorrow",
                    time = "02:00 - 04:30"
                )

                ScheduleItem(
                    day = "Wednesday",
                    time = "10:00 - 12:30"
                )

                ScheduleItem(
                    day = "Thursday",
                    time = "18:00 - 20:30"
                )
            }
        }
    }
}


// ==========================================================
// SCHEDULE ITEM
// ==========================================================

@Composable
fun ScheduleItem(
    day: String,
    time: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = day,
                fontWeight = FontWeight.Bold,
                color = SafeDark
            )

            Text(
                text = time,
                color = SafeGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// ==========================================================
// COMMUNITY MAP
// ==========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onReport: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SafeLight)
    ) {

        TopAppBar(
            title = {
                Text(
                    text = "Community Safety",
                    fontWeight = FontWeight.Bold
                )
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(15.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFDDE9E4))
                .border(
                    width = 1.dp,
                    color = SafeBorder,
                    shape = RoundedCornerShape(22.dp)
                )
        ) {

            // Map prototype area

            Text(
                text = "COMMUNITY MAP",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 25.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SafeDark
            )

            // Fake map roads

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.Center)
                    .background(Color.White)
            )

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .background(Color.White)
            )

            // Incident 1

            IncidentPin(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        start = 60.dp,
                        top = 120.dp
                    )
            )

            // Incident 2

            IncidentPin(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 65.dp)
            )

            // Incident 3

            IncidentPin(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        start = 110.dp,
                        bottom = 100.dp
                    )
            )

            Text(
                text = "3 incidents nearby",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp),
                color = SafeGrey
            )
        }

        Button(
            onClick = onReport,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SafeGreen
            ),
            shape = RoundedCornerShape(15.dp)
        ) {

            Text(
                text = "📢 REPORT AN INCIDENT",
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// ==========================================================
// MAP INCIDENT PIN
// ==========================================================

@Composable
fun IncidentPin(
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .size(35.dp)
            .clip(CircleShape)
            .background(SafeRed),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "!",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}


// ==========================================================
// REPORT INCIDENT SCREEN
// ==========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onBack: () -> Unit
) {

    var description by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("Crime")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SafeLight)
    ) {

        TopAppBar(
            title = {
                Text("Report an Incident")
            },
            navigationIcon = {

                TextButton(
                    onClick = onBack
                ) {

                    Text(
                        text = "←",
                        fontSize = 25.sp,
                        color = SafeGreen
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.padding(20.dp)
        ) {

            item {

                Text(
                    text = "Incident category",
                    fontWeight = FontWeight.Bold,
                    color = SafeDark
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row {

                    CategoryButton(
                        text = "Crime",
                        selected = selectedCategory == "Crime",
                        onClick = {
                            selectedCategory = "Crime"
                        }
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    CategoryButton(
                        text = "Suspicious",
                        selected = selectedCategory == "Suspicious",
                        onClick = {
                            selectedCategory = "Suspicious"
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                CategoryButton(
                    text = "Traffic",
                    selected = selectedCategory == "Traffic",
                    onClick = {
                        selectedCategory = "Traffic"
                    }
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("Describe what happened")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Text("📷 Add Photo")
                }

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "📍",
                            fontSize = 30.sp
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Column {

                            Text(
                                text = "Location detected",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "GPS location will be attached",
                                color = SafeGrey,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(25.dp)
                )

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SafeGreen
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Text(
                        text = "SUBMIT REPORT",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


// ==========================================================
// CATEGORY BUTTON
// ==========================================================

@Composable
fun CategoryButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    if (selected) {

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = SafeGreen
            )
        ) {

            Text(text)
        }

    } else {

        OutlinedButton(
            onClick = onClick
        ) {

            Text(text)
        }
    }
}


// ==========================================================
// SOS SCREEN
// ==========================================================

@Composable
fun SOSScreen() {

    var activated by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (activated) {
                    SafeRed
                } else {
                    Color(0xFFFFF5F5)
                }
            )
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = if (activated) "🚨" else "⚠️",
            fontSize = 70.sp
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Text(
            text = if (activated) {
                "SOS ACTIVATED"
            } else {
                "EMERGENCY SOS"
            },
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = if (activated) {
                Color.White
            } else {
                SafeRed
            },
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = if (activated) {
                "Your emergency contacts are being alerted."
            } else {
                "Press the button to send your location."
            },
            color = if (activated) {
                Color.White
            } else {
                SafeGrey
            },
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(40.dp)
        )

        Button(
            onClick = {
                activated = true
            },
            modifier = Modifier.size(190.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = SafeRed
            )
        ) {

            Text(
                text = if (activated) {
                    "ACTIVE"
                } else {
                    "SOS"
                },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(35.dp)
        )

        if (!activated) {

            Text(
                text = "Emergency contacts",
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "• Mom\n• Dad\n• Neighbourhood Watch",
                color = SafeGrey,
                textAlign = TextAlign.Center
            )
        }
    }
}


// ==========================================================
// SETTINGS SCREEN
// ==========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SafeLight)
    ) {

        item {

            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold
                    )
                }
            )

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(65.dp)
                                .clip(CircleShape)
                                .background(SafeGreen),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "K",
                                color = Color.White,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(15.dp)
                        )

                        Column {

                            Text(
                                text = "Kakuhle",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "kakuhle@example.com",
                                color = SafeGrey
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                SettingItem(
                    title = "🌍 Language",
                    subtitle = "English"
                )

                SettingItem(
                    title = "🔔 Notifications",
                    subtitle = "Loadshedding and safety alerts"
                )

                SettingItem(
                    title = "📍 Safety Alert Radius",
                    subtitle = "5 km"
                )

                SettingItem(
                    title = "🛡️ Guardian Points",
                    subtitle = "120 points"
                )

                SettingItem(
                    title = "💾 Offline Data",
                    subtitle = "Cached schedule available"
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SafeRed
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Text("LOG OUT")
                }
            }
        }
    }
}


// ==========================================================
// SETTINGS ITEM
// ==========================================================

@Composable
fun SettingItem(
    title: String,
    subtitle: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(17.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {}
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SafeDark
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = subtitle,
                    color = SafeGrey,
                    fontSize = 13.sp
                )
            }

            Text(
                text = "›",
                fontSize = 28.sp,
                color = SafeGrey
            )
        }
    }
}