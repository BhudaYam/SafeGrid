package com.example.safegrid.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Composable
fun HomeScreen(
    state: UiState,
    viewModel: SafeGridViewModel,
    onPower: () -> Unit,
    onMap: () -> Unit,
    onReport: () -> Unit,
    onSos: () -> Unit
) {
    val lang = state.lang
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> Strings.get(lang, S.GoodMorning)
        in 12..16 -> Strings.get(lang, S.GoodAfternoon)
        else -> Strings.get(lang, S.GoodEvening)
    }
    val stageText = when (state.stage) {
        null -> Strings.get(lang, S.StageUnknown)
        0 -> Strings.get(lang, S.NoLoadshedding)
        else -> Strings.format(lang, S.StageLabel, state.stage)
    }
    val next = state.schedule.firstOrNull()
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(SafeLight).padding(20.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = greeting, color = SafeGrey)
                    Text(
                        text = state.name.ifBlank { "SafeGrid" },
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        color = SafeDark
                    )
                }
                Text(text = "🛡️", fontSize = 40.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (state.banner != null) {
                Banner(Strings.get(lang, state.banner))
                Spacer(modifier = Modifier.height(12.dp))
            }
            SectionTitle(Strings.get(lang, S.PowerStatus))
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SafeGreen)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "⚡  $stageText", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = Strings.get(lang, S.NextOutage), color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                    Text(
                        text = if (next == null) Strings.get(lang, S.NotSynced) else viewModel.timeLabel(next),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (state.fromCache) Strings.get(lang, S.SourceCache) else Strings.get(lang, S.SourceLive),
                        color = SafeAmber,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onPower, colors = ButtonDefaults.buttonColors(containerColor = SafeAmber)) {
                        Text(text = Strings.get(lang, S.ViewSchedule), color = SafeDark)
                    }
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
            SectionTitle(Strings.get(lang, S.CommunitySafety))
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = Strings.format(lang, S.IncidentsNearby, state.incidents.size),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SafeDark
                    )
                    Text(text = Strings.get(lang, S.StayAware), color = SafeGrey)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        Button(onClick = onMap, colors = ButtonDefaults.buttonColors(containerColor = SafeGreen)) {
                            Text(Strings.get(lang, S.ViewMap))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(onClick = onReport) { Text(Strings.get(lang, S.Report)) }
                    }
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
            SectionTitle(Strings.get(lang, S.Emergency))
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSos,
                modifier = Modifier.fillMaxWidth().height(65.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SafeRed),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(text = "🚨  ${Strings.get(lang, S.SosButton)}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(18.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = Strings.get(lang, S.GuardianPoints), fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(
                        text = Strings.format(lang, S.PointsValue, state.points, Strings.get(lang, S.CommunityHelper)),
                        color = SafeGrey
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = Strings.get(lang, S.ConfigureSteps), color = SafeGrey, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowerScreen(state: UiState, viewModel: SafeGridViewModel) {
    val lang = state.lang
    Column(modifier = Modifier.fillMaxSize().background(SafeLight)) {
        TopAppBar(title = { Text(Strings.get(lang, S.ScheduleTitle), fontWeight = FontWeight.Bold) })
        LazyColumn(modifier = Modifier.padding(20.dp)) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SafeGreen)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = Strings.get(lang, S.YourArea), color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                        Text(text = state.area, color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (state.stage == null) {
                                Strings.get(lang, S.StageUnknown)
                            } else {
                                Strings.format(lang, S.CurrentStage, state.stage)
                            },
                            color = SafeAmber,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = Strings.get(lang, S.OfflineSchedule), color = SafeGreen, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = Strings.get(lang, S.ShowingCached), color = SafeGrey)
                        Text(
                            text = Strings.format(lang, S.LastSynced, viewModel.formatSync(state.lastSync)),
                            color = SafeGrey,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (state.fromCache) Strings.get(lang, S.SourceCache) else Strings.get(lang, S.SourceLive),
                            color = SafeDark,
                            fontWeight = FontWeight.Bold
                        )
                        if (state.pendingCount > 0) {
                            Text(text = Strings.format(lang, S.PendingSync, state.pendingCount), color = SafeAmber)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = Strings.get(lang, S.ApiResponse), fontWeight = FontWeight.Bold, color = SafeDark)
                        Text(text = viewModel.apiUrl(), color = SafeGreen, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when {
                                state.apiLoading -> Strings.get(lang, S.ApiLoading)
                                state.apiRaw.isBlank() -> Strings.get(lang, S.ApiFailed)
                                else -> state.apiRaw
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = SafeDark
                        )
                        if (state.apiDetail.isNotBlank()) {
                            Text(text = state.apiDetail, color = SafeRed, fontSize = 12.sp)
                        }
                        Text(text = Strings.get(lang, S.EstimatedNote), color = SafeGrey, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.refreshPower() },
                            colors = ButtonDefaults.buttonColors(containerColor = SafeGreen)
                        ) {
                            Text(Strings.get(lang, S.Refresh))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(state.schedule, key = { it.id }) { slot ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = viewModel.dayLabel(slot.dayKey), fontWeight = FontWeight.Bold, color = SafeDark)
                        Text(text = viewModel.timeLabel(slot), color = SafeGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(state: UiState, viewModel: SafeGridViewModel, onReport: () -> Unit) {
    val lang = state.lang
    Column(modifier = Modifier.fillMaxSize().background(SafeLight)) {
        TopAppBar(title = { Text(Strings.get(lang, S.CommunityMap), fontWeight = FontWeight.Bold) })
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .padding(15.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFDDE9E4))
                .border(1.dp, SafeBorder, RoundedCornerShape(22.dp))
        ) {
            Text(
                text = Strings.format(lang, S.IncidentsNearby, state.incidents.size),
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp),
                fontWeight = FontWeight.Bold,
                color = SafeDark
            )
            Box(modifier = Modifier.fillMaxWidth().height(2.dp).align(Alignment.Center).background(Color.White))
            Box(modifier = Modifier.width(2.dp).fillMaxSize().align(Alignment.Center).background(Color.White))
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                state.incidents.take(8).forEach { incident ->
                    val (xFraction, yFraction) = pinFractions(incident.id)
                    Box(
                        modifier = Modifier
                            .offset(maxWidth * xFraction, maxHeight * yFraction)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(SafeRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "!", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 15.dp)) {
            if (state.incidents.isEmpty()) {
                item { Text(text = Strings.get(lang, S.EmptyIncidents), color = SafeGrey) }
            }
            items(state.incidents, key = { it.id }) { incident ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = viewModel.categoryLabel(incident.category),
                            fontWeight = FontWeight.Bold,
                            color = SafeDark
                        )
                        Text(text = incident.description, color = SafeGrey)
                        Text(text = incident.area, color = SafeGrey, fontSize = 12.sp)
                        Text(
                            text = if (incident.pendingSync) Strings.get(lang, S.Pending) else Strings.get(lang, S.Synced),
                            color = if (incident.pendingSync) SafeAmber else SafeGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (!incident.photoCloudUrl.isNullOrBlank()) {
                            Text(text = incident.photoCloudUrl, color = SafeGreen, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
        Button(
            onClick = onReport,
            modifier = Modifier.fillMaxWidth().padding(15.dp).height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text(text = Strings.get(lang, S.ReportIncident), fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    state: UiState,
    onBack: () -> Unit,
    onPickPhoto: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    val lang = state.lang
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Crime") }
    Column(modifier = Modifier.fillMaxSize().background(SafeLight)) {
        TopAppBar(
            title = { Text(Strings.get(lang, S.ReportIncident)) },
            navigationIcon = {
                TextButton(onClick = onBack) { Text(text = "←", fontSize = 24.sp, color = SafeGreen) }
            }
        )
        LazyColumn(modifier = Modifier.padding(20.dp)) {
            item {
                if (state.banner != null) {
                    Text(text = Strings.get(lang, state.banner), color = SafeRed)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(text = Strings.get(lang, S.IncidentCategory), fontWeight = FontWeight.Bold, color = SafeDark)
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    CategoryChip("Crime", category, Strings.get(lang, S.Crime)) { category = "Crime" }
                    Spacer(modifier = Modifier.width(8.dp))
                    CategoryChip("Suspicious", category, Strings.get(lang, S.Suspicious)) { category = "Suspicious" }
                }
                Spacer(modifier = Modifier.height(8.dp))
                CategoryChip("Traffic", category, Strings.get(lang, S.Traffic)) { category = "Traffic" }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(Strings.get(lang, S.Describe)) },
                    modifier = Modifier.fillMaxWidth().height(140.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = onPickPhoto, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Text(
                        if (state.draftPhoto == null) Strings.get(lang, S.AddPhoto)
                        else Strings.get(lang, S.PhotoAttached)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = Strings.get(lang, S.LocationDetected), fontWeight = FontWeight.Bold)
                        Text(text = state.area, color = SafeDark)
                        Text(text = Strings.get(lang, S.LocationHelp), color = SafeGrey, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = { onSubmit(category, description) },
                    enabled = !state.busy,
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (state.busy) Strings.get(lang, S.PleaseWait) else Strings.get(lang, S.SubmitReport),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(id: String, selected: String, label: String, onClick: () -> Unit) {
    if (selected == id) {
        Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = SafeGreen)) {
            Text(label)
        }
    } else {
        OutlinedButton(onClick = onClick) { Text(label) }
    }
}

@Composable
fun SosScreen(state: UiState, onActivate: () -> Unit, onCancel: () -> Unit) {
    val lang = state.lang
    val active = state.sosActive
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (active) SafeRed else Color(0xFFFFF5F5))
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (active) "🚨" else "⚠️", fontSize = 70.sp)
        Text(
            text = Strings.get(lang, if (active) S.SosActivated else S.SosReady),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = if (active) Color.White else SafeRed,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = Strings.get(lang, if (active) S.SosSent else S.SosHelp),
            color = if (active) Color.White else SafeGrey,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = { if (active) onCancel() else onActivate() },
            modifier = Modifier.size(190.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = if (active) SafeDark else SafeRed)
        ) {
            Text(
                text = if (active) Strings.get(lang, S.CancelSos) else "SOS",
                fontSize = if (active) 16.sp else 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        if (!active) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = Strings.get(lang, S.Contacts), fontWeight = FontWeight.Bold)
            Text(
                text = "• ${Strings.get(lang, S.Mom)}\n• ${Strings.get(lang, S.Dad)}\n• ${Strings.get(lang, S.Watch)}",
                color = SafeGrey,
                textAlign = TextAlign.Center
            )
        } else if (state.banner != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = Strings.get(lang, state.banner), color = Color.White, textAlign = TextAlign.Center)
        }
    }
}
