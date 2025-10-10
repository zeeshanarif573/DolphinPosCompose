package com.retail.dolphinpos.common.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun BaseText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    fontSize: Float? = null,
    fontWeight: FontWeight = FontWeight.Medium,
    fontFamily: FontFamily = GeneralSans,
    style: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize?.sp ?: style.fontSize,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        style = style
    )
}
