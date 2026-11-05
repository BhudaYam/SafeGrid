package com.example.safegrid.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safegrid.core.PasswordHasher
import com.example.safegrid.core.Validators

@Composable
fun SplashScreen(onGetStarted: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = SafeGreen) {
        Column(
            modifier = Modifier.fillMaxSize().padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(125.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🛡️", fontSize = 65.sp)
            }
            Spacer(modifier = Modifier.height(25.dp))
            Text(text = "SAFEGRID", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = Strings.get(Lang.EN, S.Tagline),
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )
            Spacer(modifier = Modifier.height(50.dp))
            Button(
                onClick = onGetStarted,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SafeAmber)
            ) {
                Text(text = Strings.get(Lang.EN, S.GetStarted), color = SafeDark, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LoginScreen(
    state: UiState,
    onLogin: (String, String) -> Unit,
    onGoogle: () -> Unit,
    onRegister: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val lang = state.lang
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SafeLight)
            .verticalScroll(rememberScrollState())
            .padding(25.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🛡️", fontSize = 55.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(
            text = Strings.get(lang, S.WelcomeBack),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = SafeDark,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = Strings.get(lang, S.SignInContinue),
            color = SafeGrey,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (state.banner != null) {
            Text(text = Strings.get(lang, state.banner), color = SafeRed)
            Spacer(modifier = Modifier.height(12.dp))
        }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(Strings.get(lang, S.Email)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(Strings.get(lang, S.Password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        PasswordHashNote(lang, password)
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = { onLogin(email, password) },
            enabled = !state.busy,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = if (state.busy) Strings.get(lang, S.PleaseWait) else Strings.get(lang, S.Login),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onGoogle,
            enabled = !state.busy,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(Strings.get(lang, S.ContinueGoogle))
        }
        TextButton(onClick = onRegister, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = Strings.get(lang, S.NoAccount), color = SafeGreen)
        }
    }
}

@Composable
fun RegisterScreen(
    state: UiState,
    onRegister: (String, String, String) -> Unit,
    onGoogle: () -> Unit,
    onBack: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val lang = state.lang
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SafeLight)
            .verticalScroll(rememberScrollState())
            .padding(25.dp)
    ) {
        TextButton(onClick = onBack) {
            Text(text = "← ${Strings.get(lang, S.Back)}", color = SafeGreen)
        }
        Text(text = Strings.get(lang, S.CreateAccount), fontSize = 30.sp, fontWeight = FontWeight.Bold, color = SafeDark)
        Text(text = Strings.get(lang, S.JoinCommunity), color = SafeGrey)
        Spacer(modifier = Modifier.height(20.dp))
        if (state.banner != null) {
            Text(text = Strings.get(lang, state.banner), color = SafeRed)
            Spacer(modifier = Modifier.height(12.dp))
        }
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(Strings.get(lang, S.FullName)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(Strings.get(lang, S.Email)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(Strings.get(lang, S.Password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        PasswordHashNote(lang, password)
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = { onRegister(name, email, password) },
            enabled = !state.busy,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = if (state.busy) Strings.get(lang, S.PleaseWait) else Strings.get(lang, S.CreateAccount),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onGoogle, enabled = !state.busy, modifier = Modifier.fillMaxWidth()) {
            Text(Strings.get(lang, S.ContinueGoogle))
        }
    }
}

@Composable
private fun PasswordHashNote(lang: Lang, password: String) {
    Spacer(modifier = Modifier.height(10.dp))
    Text(text = Strings.get(lang, S.PasswordNotice), color = SafeGrey, fontSize = 12.sp)
    if (Validators.password(password)) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = Strings.get(lang, S.HashPreview), fontWeight = FontWeight.Bold, color = SafeDark, fontSize = 12.sp)
        Text(text = PasswordHasher.sha256(password), color = SafeGreen, fontSize = 12.sp)
    }
}
