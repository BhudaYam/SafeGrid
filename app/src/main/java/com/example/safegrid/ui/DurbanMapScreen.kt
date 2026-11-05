package com.example.safegrid.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File

private data class DurbanIncident(
    val title: String,
    val area: String,
    val type: String,
    val time: String,
    val lat: Double,
    val lon: Double
)

private val durbanIncidents = listOf(
    DurbanIncident("Vehicle break-in reported", "Durban CBD", "Crime", "20 min ago", -29.8587, 31.0218),
    DurbanIncident("Suspicious activity near taxi rank", "Berea", "Safety", "45 min ago", -29.8500, 31.0000),
    DurbanIncident("Power outage - Stage 4", "Umhlanga", "Power", "1 h ago", -29.7270, 31.0850),
    DurbanIncident("Street light vandalism", "Morningside", "Infrastructure", "2 h ago", -29.8300, 31.0050),
    DurbanIncident("Cable theft, area without power", "Pinetown", "Power", "3 h ago", -29.8167, 30.8667),
    DurbanIncident("Armed robbery reported", "Westville", "Crime", "4 h ago", -29.8333, 30.9167),
    DurbanIncident("Neighbourhood watch alert", "Chatsworth", "Safety", "5 h ago", -29.9167, 30.8833),
    DurbanIncident("Transformer fault", "Umlazi", "Power", "6 h ago", -29.9667, 30.8833),
    DurbanIncident("Road blocked by protest", "Phoenix", "Safety", "Yesterday", -29.7000, 31.0000),
    DurbanIncident("Burst water main", "Glenwood", "Infrastructure", "Yesterday", -29.8730, 31.0000)
)

private fun typeColor(type: String): Color = when (type) {
    "Crime" -> Color(0xFFD32F2F)
    "Power" -> Color(0xFFF9A825)
    "Safety" -> Color(0xFF1976D2)
    else -> Color(0xFF7B1FA2)
}

@Composable
fun DurbanMapScreen(onReport: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        val base = File(context.cacheDir, "osmdroid").also { it.mkdirs() }
        val tiles = File(base, "tiles").also { it.mkdirs() }
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            osmdroidBasePath = base
            osmdroidTileCache = tiles
        }
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(11.0)
            controller.setCenter(GeoPoint(-29.8300, 30.9800))
            durbanIncidents.forEach { i ->
                val marker = Marker(this)
                marker.position = GeoPoint(i.lat, i.lon)
                marker.title = i.title
                marker.snippet = "${i.area} - ${i.type} - ${i.time}"
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                overlays.add(marker)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Incident Map",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Durban, KwaZulu-Natal - sample data",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Button(onClick = onReport) { Text("Report") }
        }

        AndroidView(
            factory = { mapView },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Text(
            "Nearby incidents (${durbanIncidents.size})",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 12.dp)
        ) {
            items(durbanIncidents) { incident ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            mapView.controller.setZoom(14.0)
                            mapView.controller.animateTo(GeoPoint(incident.lat, incident.lon))
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(typeColor(incident.type))
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(incident.title, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${incident.area} - ${incident.type} - ${incident.time}",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}