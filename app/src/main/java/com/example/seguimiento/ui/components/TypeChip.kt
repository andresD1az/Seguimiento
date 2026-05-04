package com.example.seguimiento.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun typeColor(type: String): Color = when (type.lowercase()) {
    "fire" -> Color(0xFFFF6B35)
    "water" -> Color(0xFF4A90D9)
    "grass" -> Color(0xFF56C02B)
    "electric" -> Color(0xFFF7D02C)
    "psychic" -> Color(0xFFF95587)
    "ice" -> Color(0xFF96D9D6)
    "dragon" -> Color(0xFF6F35FC)
    "dark" -> Color(0xFF705746)
    "fairy" -> Color(0xFFD685AD)
    "fighting" -> Color(0xFFC22E28)
    "poison" -> Color(0xFFA33EA1)
    "ground" -> Color(0xFFE2BF65)
    "flying" -> Color(0xFFA98FF3)
    "bug" -> Color(0xFFA6B91A)
    "rock" -> Color(0xFFB6A136)
    "ghost" -> Color(0xFF735797)
    "steel" -> Color(0xFFB7B7CE)
    "normal" -> Color(0xFFA8A77A)
    else -> Color(0xFF888888)
}

@Composable
fun TypeChip(type: String, modifier: Modifier = Modifier) {
    Text(
        text = type.replaceFirstChar { it.uppercase() },
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(
                color = typeColor(type),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 3.dp)
    )
}
