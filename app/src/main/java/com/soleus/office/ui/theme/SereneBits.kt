package com.soleus.office.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Tüm sekmelerde ortak üst logo satırı: "Soleus" (Quicksand, adaçayı) + sağda profil ikonu.
 */
@Composable
fun SereneHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SerenePrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "S",
                fontFamily = Quicksand,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = SerenePrimary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Soleus",
            fontFamily = Quicksand,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = SerenePrimary
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SerenePrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profil",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/** Adaçayı hap buton (tam yuvarlak, en az 48dp dokunma hedefi). */
@Composable
fun SerenePillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 48.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = SerenePrimary,
            contentColor = Color.White,
            disabledContainerColor = SerenePrimaryFixedDim,
            disabledContentColor = SereneOnPrimaryContainer
        )
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.6.sp),
            color = if (enabled) Color.White else SereneOnPrimaryContainer
        )
    }
}

/** Egzersiz -> ikon eşlemesi (11 hareket). */
fun exerciseIcon(id: String): ImageVector = when (id) {
    "soleus-pushup" -> Icons.Filled.FitnessCenter
    "ankle-circle-calf-raise" -> Icons.AutoMirrored.Filled.DirectionsWalk
    "hip-squeeze" -> Icons.Filled.Accessibility
    "seated-leg-extension" -> Icons.Filled.Chair
    "neck-side-stretch" -> Icons.Filled.SelfImprovement
    "shoulder-shrug-roll" -> Icons.Filled.WorkspacePremium
    "scapular-squeeze" -> Icons.Filled.Favorite
    "seated-trunk-rotation" -> Icons.Filled.Spa
    "wrist-forearm-stretch" -> Icons.Filled.Air
    "eye-202020-breath-444" -> Icons.Filled.Visibility
    "standup-mini-set" -> Icons.Filled.LightMode
    else -> Icons.Filled.WbSunny
}

/** Yuvarlak ikon balonu (açık yeşil varsayılan). */
@Composable
fun IconBubble(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    container: Color = SerenePrimaryContainer,
    content: Color = SereneOnPrimaryContainer,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(container),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = content,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}
