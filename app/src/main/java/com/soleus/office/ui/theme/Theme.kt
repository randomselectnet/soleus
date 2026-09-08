package com.soleus.office.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Soleus renk paleti: kağıt zemin, mürekkep metin, turuncu aksan.
val Kagit = Color(0xFFFAF6EF)
val Murekkep = Color(0xFF1A1E1B)
val Aksan = Color(0xFFFF5C1A)

private val SoleusRenkler = lightColorScheme(
    primary = Aksan,
    onPrimary = Kagit,
    secondary = Murekkep,
    onSecondary = Kagit,
    tertiary = Aksan,
    onTertiary = Kagit,
    background = Kagit,
    onBackground = Murekkep,
    surface = Kagit,
    onSurface = Murekkep
)

// Not: özel font dosyası yok; sistem serif (başlık) + sistem sans (gövde).
// Inter / Roboto / Space Grotesk kullanılmaz.
private val SoleusTipografi = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        color = Murekkep
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        color = Murekkep
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = Murekkep
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = Murekkep
    )
)

@Composable
fun SoleusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SoleusRenkler,
        typography = SoleusTipografi,
        content = content
    )
}
