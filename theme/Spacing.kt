package org.babetech.borastock.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val extraSmall: Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp
) {
    companion object {
        fun regular(scale: Float = 1f): Spacing = Spacing(
            extraSmall = (4 * scale).dp,
            small = (8 * scale).dp,
            medium = (16 * scale).dp,
            large = (24 * scale).dp,
            extraLarge = (32 * scale).dp
        )
    }
}
