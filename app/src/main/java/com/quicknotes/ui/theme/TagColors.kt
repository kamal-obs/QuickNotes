package com.quicknotes.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class TagColors(
    val personal: Color,
    val work: Color,
    val default: Color
)

val LightTagColors = TagColors(
    personal = Color(0xFF0D9488),
    work = Amber600,
    default = Slate600
)

val DarkTagColors = TagColors(
    personal = Color(0xFF5EEAD4),
    work = Amber300,
    default = Slate300
)

val LocalTagColors = staticCompositionLocalOf { LightTagColors }

@Composable
fun tagColor(tag: String): Color {
    val colors = LocalTagColors.current
    return when (tag.lowercase()) {
        "personal" -> colors.personal
        "work" -> colors.work
        else -> colors.default
    }
}
