package org.babetech.borastock.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
//import androidx.compose.material3.dynamicDarkColorScheme
//import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext



@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecifiedScheme = ColorFamily(
    Color.Unspecified, Color.Unspecified,
    Color.Unspecified, Color.Unspecified
)

@Composable
fun AppTheme(
    themeSelection: String = "system", // "light", "dark", "system"
    fontScaleValue: Float = 1f,
    uiScaleValue: Float = 1f,
    content: @Composable () -> Unit
) {

    val systemDark = isSystemInDarkTheme()

    val typography = getAppTypography() // Appel de la nouvelle fonction



    // Gestion du mode sombre basé sur la sélection
    val isDark = remember(themeSelection, systemDark) {
        mutableStateOf(
            when (themeSelection) {
                "dark" -> true
                "light" -> false
                else -> systemDark
            }
        )
    }

    val colorScheme: ColorScheme = if (isDark.value) darkScheme else lightScheme


    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}