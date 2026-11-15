package eu.kanade.presentation.reader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.components.GlassSettings

@Composable
fun readerBarColor(): Color {
    GlassSettings.load(LocalContext.current)
    val base = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    val dark = isSystemInDarkTheme()
    return if (GlassSettings.enabled) {
        base.copy(alpha = 0.38f)
    } else {
        base.copy(alpha = if (dark) 0.9f else 0.95f)
    }
}

@Composable
fun Modifier.readerBarSurface(shape: Shape): Modifier {
    val color = readerBarColor()
    val filled = this
        .clip(shape)
        .background(color)
    return if (GlassSettings.enabled) {
        filled.border(
            width = 1.dp,
            brush = Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.50f),
                    Color.White.copy(alpha = 0.08f),
                ),
            ),
            shape = shape,
        )
    } else {
        filled
    }
}
