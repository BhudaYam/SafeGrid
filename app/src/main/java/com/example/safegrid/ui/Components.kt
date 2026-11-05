package com.example.safegrid.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val SafeGreen = Color(0xFF0B6E4F)
val SafeAmber = Color(0xFFF2B134)
val SafeRed = Color(0xFFE63946)
val SafeDark = Color(0xFF12372A)
val SafeLight = Color(0xFFF5F7F6)
val SafeGrey = Color(0xFF66736D)
val SafeBorder = Color(0xFFD7E1DC)

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = SafeGrey
    )
}

@Composable
fun Banner(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(14.dp),
            color = SafeDark
        )
    }
}

@Composable
fun SettingRow(title: String, subtitle: String, onClick: (() -> Unit)? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(17.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SafeDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, color = SafeGrey, fontSize = 13.sp)
            }
            if (onClick != null) {
                Text(text = "›", fontSize = 28.sp, color = SafeGrey)
            }
        }
    }
}

fun pinFractions(id: String): Pair<Float, Float> {
    val hash = id.hashCode()
    val x = 0.12f + ((hash ushr 16) and 0xFF) / 255f * 0.7f
    val y = 0.18f + (hash and 0xFF) / 255f * 0.55f
    return x to y
}
