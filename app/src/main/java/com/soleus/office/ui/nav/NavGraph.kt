package com.soleus.office.ui.nav

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.soleus.office.data.ContentLoader
import com.soleus.office.data.db.AppDb
import com.soleus.office.data.db.ReminderSettings
import com.soleus.office.data.db.SessionLog
import com.soleus.office.ui.detail.ExerciseDetailScreen
import com.soleus.office.ui.home.HomeScreen
import com.soleus.office.ui.list.ExerciseListScreen
import com.soleus.office.ui.onboarding.OnboardingScreen
import com.soleus.office.ui.settings.SettingsScreen
import com.soleus.office.ui.stats.StatsScreen
import com.soleus.office.ui.stats.last7DayCounts
import com.soleus.office.ui.stats.streakFromLogs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

object SoleusRotalari {
    const val KARSILAMA = "onboarding"
    const val ANA_SAYFA = "home"
    const val LISTE = "list"
    const val DETAY = "detail/{id}"
    const val ISTATISTIK = "stats"
    const val AYARLAR = "settings"

    fun detay(id: String): String = "detail/$id"
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val appCtx = LocalContext.current
    // Bildirim tap deep-link (soleus://detail/{id}) karşılama.
    LaunchedEffect(Unit) {
        val intent = (appCtx as? Activity)?.intent
        if (intent?.action == Intent.ACTION_VIEW) {
            navController.handleDeepLink(intent)
        }
    }
    NavHost(
        navController = navController,
        startDestination = SoleusRotalari.ANA_SAYFA
    ) {
        composable(SoleusRotalari.KARSILAMA) {
            OnboardingScreen(
                onGrant = {
                    navController.navigate(SoleusRotalari.ANA_SAYFA) {
                        popUpTo(SoleusRotalari.KARSILAMA) { inclusive = true }
                    }
                }
            )
        }
        composable(SoleusRotalari.ANA_SAYFA) {
            val ctx = LocalContext.current
            val egzersizler = remember { ContentLoader.load(ctx) }
            var streak by remember { mutableIntStateOf(0) }
            LaunchedEffect(Unit) {
                val logs = withContext(Dispatchers.IO) {
                    AppDb.get(ctx).logDao().recent(365)
                }
                streak = streakFromLogs(logs, LocalDate.now())
            }
            val siradaki = egzersizler.firstOrNull()
            if (siradaki != null) {
                HomeScreen(
                    nextName = siradaki.trName,
                    nextDurationSec = siradaki.durationSec,
                    streak = streak,
                    onStart = { navController.navigate(SoleusRotalari.detay(siradaki.id)) },
                    onOpenList = { navController.navigate(SoleusRotalari.LISTE) }
                )
            }
        }
        composable(SoleusRotalari.LISTE) {
            val ctx = LocalContext.current
            val egzersizler = remember { ContentLoader.load(ctx) }
            ExerciseListScreen(
                exercises = egzersizler,
                onOpen = { id -> navController.navigate(SoleusRotalari.detay(id)) }
            )
        }
        composable(
            route = SoleusRotalari.DETAY,
            deepLinks = listOf(navDeepLink { uriPattern = "soleus://detail/{id}" })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            val ctx = LocalContext.current
            val scope = rememberCoroutineScope()
            val egzersizler = remember { ContentLoader.load(ctx) }
            val egzersiz = egzersizler.firstOrNull { it.id == id }
                ?: egzersizler.firstOrNull()
            if (egzersiz != null) {
                ExerciseDetailScreen(
                    exercise = egzersiz,
                    onDone = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                AppDb.get(ctx).logDao().insert(
                                    SessionLog(
                                        exerciseId = egzersiz.id,
                                        timestampMillis = System.currentTimeMillis(),
                                        durationDoneSec = egzersiz.durationSec
                                    )
                                )
                            }
                        }
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(SoleusRotalari.ISTATISTIK) {
            val ctx = LocalContext.current
            var streak by remember { mutableIntStateOf(0) }
            var counts by remember { mutableStateOf(List(7) { 0 }) }
            LaunchedEffect(Unit) {
                val logs = withContext(Dispatchers.IO) {
                    AppDb.get(ctx).logDao()
                        .logsSince(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)
                }
                val today = LocalDate.now()
                streak = streakFromLogs(logs, today)
                counts = last7DayCounts(logs, today)
            }
            StatsScreen(
                streak = streak,
                weeklyCounts = counts
            )
        }
        composable(SoleusRotalari.AYARLAR) {
            val ctx = LocalContext.current
            var ayar by remember { mutableStateOf<ReminderSettings?>(null) }
            LaunchedEffect(Unit) {
                ayar = withContext(Dispatchers.IO) {
                    AppDb.get(ctx).settingsDao().get()
                }
            }
            val a = ayar
            if (a == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Yükleniyor…",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                SettingsScreen(
                    workStartMin = a.workStartMin,
                    workEndMin = a.workEndMin,
                    intervalMin = a.intervalMin,
                    onSaved = { navController.popBackStack() }
                )
            }
        }
    }
}
