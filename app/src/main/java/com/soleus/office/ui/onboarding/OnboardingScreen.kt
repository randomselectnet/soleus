package com.soleus.office.ui.onboarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.soleus.office.ui.theme.Kagit

/**
 * Karşılama ekranı: bildirim izni açıklaması (TR) + izin isteği.
 * Reddedilirse uygulama içi banner + Ayarlar deep-link gösterilir.
 */
@Composable
fun OnboardingScreen(
    onGrant: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val needsPermission = Build.VERSION.SDK_INT >= 33
    var denied by remember { mutableStateOf(false) }

    fun alreadyGranted(): Boolean =
        !needsPermission ||
            ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            denied = false
            onGrant()
        } else {
            denied = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Hoş geldin",
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "Soleus, mesai saatlerinde her saat başı kısa bir ofis hareketi hatırlatır. " +
                "Hatırlatmalar için bildirim izni gerekli.",
            style = MaterialTheme.typography.bodyLarge
        )
        Button(
            onClick = {
                if (alreadyGranted()) onGrant()
                else launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(text = "Bildirimlere izin ver")
        }
        if (denied) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Kagit)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Bildirim izni verilmedi. Hatırlatmalar çalışmaz.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", ctx.packageName, null)
                            ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                            ctx.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                    ) {
                        Text(text = "Ayarları aç")
                    }
                }
            }
        }
        TextButton(
            onClick = onGrant,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(text = "Atla")
        }
    }
}
