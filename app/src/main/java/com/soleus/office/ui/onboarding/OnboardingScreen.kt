package com.soleus.office.ui.onboarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.soleus.office.ui.theme.Kagit
import com.soleus.office.ui.theme.Quicksand
import com.soleus.office.ui.theme.SereneCard
import com.soleus.office.ui.theme.SerenePrimary
import kotlinx.coroutines.launch

/**
 * Karşılama: tek rota içinde yatay pager (3 sayfa) + koşullu izin adımı.
 * NavGraph guard ve onboarding_done mantığı değişmez.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onGrant: () -> Unit = {},
    onSilentStart: () -> Unit = onGrant
) {
    var adim by rememberSaveable { mutableStateOf(KarsilamaAdimi.SAYFA_1) }
    val pagerState = rememberPagerState(pageCount = { KARSILAMA_SAYFALARI.size })
    val kapsam = rememberCoroutineScope()

    // Sistem-geri: izindeyken sayfa 3'e, sayfalardayken önceki sayfaya döner.
    BackHandler(enabled = adim == KarsilamaAdimi.IZIN) {
        adim = KarsilamaAdimi.SAYFA_3
    }
    BackHandler(enabled = adim != KarsilamaAdimi.IZIN && pagerState.currentPage > 0) {
        kapsam.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (adim == KarsilamaAdimi.IZIN) {
            IzinAdimi(
                onGrant = onGrant,
                onSilentStart = onSilentStart
            )
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { sayfa ->
                KarsilamaSayfaIcerigi(
                    sayfa = KARSILAMA_SAYFALARI[sayfa],
                    sayfaNo = sayfa,
                    onAtla = onGrant,
                    onCta = { adim = KarsilamaAdimi.IZIN }
                )
            }
            SayfaNoktalari(sayfa = pagerState.currentPage, toplam = KARSILAMA_SAYFALARI.size)
        }
    }
}

@Composable
private fun KarsilamaSayfaIcerigi(
    sayfa: KarsilamaSayfasi,
    sayfaNo: Int,
    onAtla: () -> Unit,
    onCta: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = sayfa.baslik,
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = Quicksand
        )
        SereneCard(modifier = Modifier.fillMaxWidth()) {
            when (sayfaNo) {
                0 -> EnerjiCubuguGorseli()
                1 -> NefesHalkasiTeaser()
                else -> UcIkonSirasi()
            }
        }
        sayfa.govde.forEach { cumle ->
            Text(
                text = cumle,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        if (sayfa.gecis.isNotBlank()) {
            Text(
                text = sayfa.gecis,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        if (sayfaNo == KARSILAMA_SAYFALARI.size - 1) {
            Button(
                onClick = onCta,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(text = CTA_UZUN)
            }
        }
        TextButton(
            onClick = onAtla,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(text = "Atla")
        }
    }
}

/** 3 nokta ilerleme göstergesi (izin ekranında yok). */
@Composable
private fun SayfaNoktalari(sayfa: Int, toplam: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(toplam) { i ->
            Box(
                modifier = Modifier
                    .size(if (i == sayfa) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (i == sayfa) SerenePrimary
                        else SerenePrimary.copy(alpha = 0.25f)
                    )
            )
        }
    }
}

/** İzin adımı: başlık + 1 cümle gerekçe + birincil/ikincil buton + ret kartı. */
@Composable
private fun IzinAdimi(
    onGrant: () -> Unit,
    onSilentStart: () -> Unit
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

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = IZIN_BASLIK,
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = Quicksand
        )
        SereneCard(modifier = Modifier.fillMaxWidth()) {
            ZilBaloncukGorseli()
        }
        Text(
            text = IZIN_GEREKCE,
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
            Text(text = IZIN_BUTON)
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
            onClick = onSilentStart,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(text = IZIN_RET_BUTON)
        }
    }
}
