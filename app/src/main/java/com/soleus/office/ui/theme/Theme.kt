package com.soleus.office.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Serene Habit paleti (DESIGN.md + spec Bölüm 7, açık tema) ──
val SereneBackground = Color(0xFFF7FAF8)
val SereneOnBackground = Color(0xFF181C1C)
val SereneSurface = Color(0xFFFFFFFF)
val SereneOnSurface = Color(0xFF181C1C)
val SerenePrimary = Color(0xFF4A654F)
val SereneOnPrimary = Color(0xFFFFFFFF)
val SerenePrimaryContainer = Color(0xFFCCEACF)
val SereneOnPrimaryContainer = Color(0xFF253F2B)
val SereneSecondary = Color(0xFF5E5F5C)
val SereneOnSecondary = Color(0xFFFFFFFF)
val SereneTertiary = Color(0xFF8C4E35)
val SereneOnTertiary = Color(0xFFFFFFFF)
val SereneTertiaryContainer = Color(0xFFDC9073)
val SereneOnTertiaryContainer = Color(0xFF5E2A14)
val SereneOutline = Color(0xFF737972)
val SereneOutlineVariant = Color(0xFFC2C8C0)
val SereneSurfaceContainerLow = Color(0xFFF1F4F2)
val SereneError = Color(0xFFBA1A1A)
val SereneOnError = Color(0xFFFFFFFF)
val SereneErrorContainer = Color(0xFFFFDAD6)
val SereneOnErrorContainer = Color(0xFF93000A)

// R1 geçiş takma adları: eski ekranlar R2+ dalgalarında Serene renklerine taşınacak.
// Davranış korunur; derleme kırılmasın diye birebir karşılıklar burada durur.
val Kagit = SereneBackground
val Murekkep = SereneOnBackground
val Aksan = SerenePrimary

private val SereneRenkler = lightColorScheme(
    primary = SerenePrimary,
    onPrimary = SereneOnPrimary,
    primaryContainer = SerenePrimaryContainer,
    onPrimaryContainer = SereneOnPrimaryContainer,
    secondary = SereneSecondary,
    onSecondary = SereneOnSecondary,
    tertiary = SereneTertiary,
    onTertiary = SereneOnTertiary,
    tertiaryContainer = SereneTertiaryContainer,
    onTertiaryContainer = SereneOnTertiaryContainer,
    background = SereneBackground,
    onBackground = SereneOnBackground,
    surface = SereneSurface,
    onSurface = SereneOnSurface,
    surfaceContainerLow = SereneSurfaceContainerLow,
    outline = SereneOutline,
    outlineVariant = SereneOutlineVariant,
    error = SereneError,
    onError = SereneOnError,
    errorContainer = SereneErrorContainer,
    onErrorContainer = SereneOnErrorContainer
)

// Tipografi: başlıklar Quicksand 600, gövde/etiket Inter. Ağır (Black) ağırlık yok.
private val SereneTipografi = Typography(
    headlineLarge = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.26).sp,
        color = SereneOnBackground
    ),
    headlineMedium = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        color = SereneOnBackground
    ),
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        color = SereneOnBackground
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = SereneOnBackground
    ),
    labelLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = SereneOnBackground
    ),
    labelMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = SereneOnBackground
    ),
    labelSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.6.sp,
        color = SereneOnBackground
    )
)

// Şekiller: kart 32dp radius; butonlar çağrı noktasında tam hap (CircleShape) kullanır.
private val SereneSekiller = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(32.dp),
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun SoleusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SereneRenkler,
        typography = SereneTipografi,
        shapes = SereneSekiller,
        content = content
    )
}
