package com.soleus.office.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Serene kart şekli: 32dp radius, bordürsüz.
val SereneCardShape = RoundedCornerShape(32.dp)

/**
 * Serene kart: beyaz zemin, 32dp radius, bordürsüz, yumuşak gölge, 24dp iç padding.
 * Sonraki dalgalar (Bugün/Hareketler/Geçmiş/Ayarlar) bunu kullanacak.
 */
@Composable
fun SereneCard(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 24.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.shadow(
            elevation = 8.dp,
            shape = SereneCardShape,
            clip = false,
            ambientColor = Color.Black.copy(alpha = 0.04f),
            spotColor = Color.Black.copy(alpha = 0.04f)
        ),
        shape = SereneCardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
