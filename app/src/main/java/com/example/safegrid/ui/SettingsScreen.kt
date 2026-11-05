package com.example.safegrid.ui

import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: UiState,
    viewModel: SafeGridViewModel,
    onRequestNotifications: () -> Unit
) {
    val lang = state.lang
    var showLanguages by remember { mutableStateOf(false) }
    var area by remember(state.area) { mutableStateOf(state.area) }
    var name by remember(state.name) { mutableStateOf(state.name) }
    var radius by remember(state.radiusKm) { mutableFloatStateOf(state.radiusKm.toFloat()) }
    val photo = state.incidents.firstOrNull { !it.photoCloudUrl.isNullOrBlank() }?.photoCloudUrl

    if (showLanguages) {
        AlertDialog(
            onDismissRequest = { showLanguages = false },
            title = { Text(Strings.get(lang, S.Language)) },
            text = {
                Column {
                    Lang.entries.forEach { option ->
                        TextButton(onClick = {
                            viewModel.setLanguage(option)
                            showLanguages = false
                        }) {
                            Text(option.label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguages = false }) {
                    Text(Strings.get(lang, S.Back))
                }
            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(SafeLight)) {
        item {
            TopAppBar(title = { Text(Strings.get(lang, S.Settings), fontWeight = FontWeight.Bold) })
            Column(modifier = Modifier.padding(20.dp)) {
                if (state.banner != null) {
                    Banner(Strings.get(lang, state.banner))
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        BoxInitial(state.name)
                        Spacer(modifier = Modifier.width(15.dp))
                        Column {
                            Text(
                                text = state.name.ifBlank { Strings.get(lang, S.NotSignedIn) },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = state.email, color = SafeGrey)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(Strings.get(lang, S.FullName)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text(Strings.get(lang, S.Area)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${Strings.get(lang, S.Radius)}: ${Strings.format(lang, S.RadiusValue, radius.toInt())}",
                    fontWeight = FontWeight.Bold,
                    color = SafeDark
                )
                Slider(
                    value = radius,
                    onValueChange = { radius = it },
                    valueRange = 1f..20f,
                    steps = 18,
                    onValueChangeFinished = { viewModel.setRadius(radius.toInt()) }
                )
                Button(
                    onClick = { viewModel.saveArea(area, name) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SafeGreen)
                ) {
                    Text(Strings.get(lang, S.Save))
                }
                Spacer(modifier = Modifier.height(12.dp))
                SettingRow(Strings.get(lang, S.Language), lang.label) { showLanguages = true }
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    shape = RoundedCornerShape(17.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = Strings.get(lang, S.Notifications), fontWeight = FontWeight.Bold, color = SafeDark)
                            Text(text = Strings.get(lang, S.NotifSub), color = SafeGrey, fontSize = 13.sp)
                        }
                        Switch(
                            checked = state.notifications,
                            onCheckedChange = { enabled ->
                                if (enabled) onRequestNotifications()
                                viewModel.setNotifications(enabled)
                            }
                        )
                    }
                }
                SettingRow(
                    Strings.get(lang, S.OfflineData),
                    Strings.format(lang, S.PendingSync, state.pendingCount)
                )
                OutlinedButton(onClick = { viewModel.syncNow() }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (state.busy) Strings.get(lang, S.PleaseWait) else Strings.get(lang, S.SyncNow))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = { viewModel.sendTestNotification() }, modifier = Modifier.fillMaxWidth()) {
                    Text(Strings.get(lang, S.TestNotification))
                }
                Spacer(modifier = Modifier.height(18.dp))
                SectionTitle(Strings.get(lang, S.CloudStatus))
                Spacer(modifier = Modifier.height(8.dp))
                CloudCard(state, viewModel, photo)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SafeRed),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(Strings.get(lang, S.LogOut))
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun BoxInitial(name: String) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.size(65.dp).clip(CircleShape).background(SafeGreen),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.uppercase() ?: "S",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CloudCard(state: UiState, viewModel: SafeGridViewModel, photoUrl: String?) {
    val lang = state.lang
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (state.cloudReady) Strings.get(lang, S.CloudReady) else Strings.get(lang, S.CloudNotReady),
                fontWeight = FontWeight.Bold,
                color = if (state.cloudReady) SafeGreen else SafeRed
            )
            if (!state.cloudReady) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = Strings.get(lang, S.FirebaseMissing), color = SafeGrey, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = Strings.get(lang, S.AuthService), fontWeight = FontWeight.Bold, color = SafeDark)
            Text(text = "${Strings.get(lang, S.SignedInAs)} ${state.email.ifBlank { "—" }}", color = SafeGrey, fontSize = 13.sp)
            Text(text = "${Strings.get(lang, S.Uid)}: ${state.uid.ifBlank { "—" }}", color = SafeGrey, fontSize = 13.sp)
            Text(text = "${Strings.get(lang, S.Provider)}: ${viewModel.providerLabel()}", color = SafeGrey, fontSize = 13.sp)
            Text(text = Strings.get(lang, S.PasswordStorage), color = SafeGrey, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = Strings.get(lang, S.NoSqlDatabase), fontWeight = FontWeight.Bold, color = SafeDark)
            Text(text = Strings.get(lang, S.UsersCollection), color = SafeGrey, fontSize = 13.sp)
            Text(text = Strings.get(lang, S.IncidentsCollection), color = SafeGrey, fontSize = 13.sp)
            Text(text = Strings.get(lang, S.AlertsCollection), color = SafeGrey, fontSize = 13.sp)
            Text(
                text = "${Strings.get(lang, S.LastWrite)}: ${state.lastCloud.ifBlank { "—" }}",
                color = SafeGrey,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = Strings.get(lang, S.BlobStorage), fontWeight = FontWeight.Bold, color = SafeDark)
            Text(
                text = photoUrl ?: Strings.get(lang, S.NoPhotoYet),
                color = if (photoUrl == null) SafeGrey else SafeGreen,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (state.pendingCount > 0) Strings.get(lang, S.WorkingOffline) else Strings.get(lang, S.Connected),
                color = SafeDark,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
