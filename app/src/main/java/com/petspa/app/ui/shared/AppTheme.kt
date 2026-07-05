package com.petspa.app.ui.shared

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import com.petspa.app.R

// Màu theme từ Figma CSS
object PetSpaColors {
    val PetPink = Color(0xFFFFB6C1)
    val PetPinkHover = Color(0xFFFF99B0)
    val PetPinkSurface = Color(0xFFFFF0F3)
    val PetPinkBorder = Color(0xFFFFD6DF)
    val PetPinkDeep = Color(0xFFE8789A)
    val AccentOwner = Color(0xFFFF8FA3)
    val OwnerBackground = Color(0xFFFFF8F9)
    val Background = Color(0xFFFFFFFF)
    val Foreground = Color(0xFF2D2D2D)
    val MutedForeground = Color(0xFF717182)
    val Destructive = Color(0xFFD4183D)
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val InputBackground = Color(0xFFF3F3F5)
}

// Google Fonts Provider cho Nunito
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val NunitoFont = GoogleFont("Nunito")

val NunitoFontFamily = FontFamily(
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.Bold)
)

// Thiết lập Typography sử dụng font Nunito làm mặc định
val AppTypography = Typography(
    displayLarge = Typography().displayLarge.copy(fontFamily = NunitoFontFamily),
    displayMedium = Typography().displayMedium.copy(fontFamily = NunitoFontFamily),
    displaySmall = Typography().displaySmall.copy(fontFamily = NunitoFontFamily),
    headlineLarge = Typography().headlineLarge.copy(fontFamily = NunitoFontFamily),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = NunitoFontFamily),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = NunitoFontFamily),
    titleLarge = Typography().titleLarge.copy(fontFamily = NunitoFontFamily),
    titleMedium = Typography().titleMedium.copy(fontFamily = NunitoFontFamily),
    titleSmall = Typography().titleSmall.copy(fontFamily = NunitoFontFamily),
    bodyLarge = Typography().bodyLarge.copy(fontFamily = NunitoFontFamily),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = NunitoFontFamily),
    bodySmall = Typography().bodySmall.copy(fontFamily = NunitoFontFamily),
    labelLarge = Typography().labelLarge.copy(fontFamily = NunitoFontFamily),
    labelMedium = Typography().labelMedium.copy(fontFamily = NunitoFontFamily),
    labelSmall = Typography().labelSmall.copy(fontFamily = NunitoFontFamily)
)

private val LightColorScheme = lightColorScheme(
    primary = PetSpaColors.PetPinkDeep,
    onPrimary = Color.White,
    secondary = PetSpaColors.PetPink,
    tertiary = PetSpaColors.AccentOwner,
    background = PetSpaColors.Background,
    surface = PetSpaColors.Background,
    onBackground = PetSpaColors.Foreground,
    onSurface = PetSpaColors.Foreground,
    error = PetSpaColors.Destructive
)

@Composable
fun PetSpaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        shapes = Shapes(medium = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)),
        content = content
    )
}
