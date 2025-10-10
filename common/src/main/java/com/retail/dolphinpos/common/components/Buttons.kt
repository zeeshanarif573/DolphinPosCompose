package com.retail.dolphinpos.common.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.retail.dolphinpos.common.R

@Composable
fun BaseButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    debounceTimeMs: Long = 500L,
    onClick: () -> Unit
) {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    Button(
        modifier = modifier.height(35.dp),
        onClick = {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= debounceTimeMs) {
                lastClickTime = currentTime
                onClick()
            }
        },
        enabled = enabled,
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.primary),
            contentColor = Color.White,
            disabledContainerColor = Color.Gray.copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        contentPadding = PaddingValues(horizontal = 60.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = GeneralSans,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        )
    }
}
