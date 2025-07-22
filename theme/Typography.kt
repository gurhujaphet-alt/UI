package org.babetech.borastock.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import borastock.composeapp.generated.resources.OpenSans_Bold
import borastock.composeapp.generated.resources.OpenSans_Medium
import borastock.composeapp.generated.resources.OpenSans_Regular
import borastock.composeapp.generated.resources.Res
import borastock.composeapp.generated.resources.Roboto_Black
import borastock.composeapp.generated.resources.Roboto_Bold
import borastock.composeapp.generated.resources.Roboto_Medium
import borastock.composeapp.generated.resources.Roboto_Regular
import borastock.composeapp.generated.resources.noto_sans_black
import borastock.composeapp.generated.resources.noto_sans_bold
import borastock.composeapp.generated.resources.noto_sans_medium
import borastock.composeapp.generated.resources.noto_sans_regular

import org.jetbrains.compose.resources.Font

// 1. Définition de la famille de polices Roboto personnalisée
@Composable
fun openSansFamily() = FontFamily(
    Font(Res.font.OpenSans_Regular, FontWeight.Normal),
    Font(Res.font.OpenSans_Medium, FontWeight.Medium),
    Font(Res.font.OpenSans_Bold, FontWeight.Bold),
    //Font(Res.font.Roboto_Black, FontWeight.Black)
)

// 2. Création de l'objet Typography de Material 3 avec les styles personnalisés
@Composable
fun getAppTypography(): Typography {
    val roboto = openSansFamily()

    return Typography(
        displayLarge = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Black,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Black,
            fontSize = 45.sp,
            lineHeight = 52.sp
        ),
        displaySmall = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Black,
            fontSize = 36.sp,
            lineHeight = 44.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 32.sp
        ),
        titleLarge = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            lineHeight = 28.sp
        ),
        titleMedium = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelLarge = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        )
    )
}



