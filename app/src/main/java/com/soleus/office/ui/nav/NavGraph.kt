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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.soleus.office.ui.ExerciseViewModel
import com.soleus.office.ui.SettingsViewModel
import com.soleus.office.ui.StatsViewModel
import com.soleus.office.ui.detail.ExerciseDetailScreen
import com.soleus.office.ui.home.HomeScreen
import com.soleus.office.ui.list.ExerciseListScreen
import com.soleus.office.ui.onboarding.OnboardingScreen
import com.soleus.office.ui.settings.SettingsScreen
import com.soleus.office.ui.stats.StatsScreen

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
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    vm: ExerciseViewModel = viewModel()
) {
    val appCtx = LocalContext.current
    val egzersizler by vm.exercises.collectAsState()
    val streak by vm.streak.collectAsState()
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
                    appCtx.getSharedPreferences("soleus", android.content.Context.MODE_PRIVATE)
                        .edit().putBoolean("onboarding_done", true).apply()
                    navController.navigate(SoleusRotalari.ANA_SAYFA) {
                        popUpTo(SoleusRotalari.KARSILAMA) { inclusive = true }
                    }
                }
            )
        }
        composable(SoleusRotalari.ANA_SAYFA) {
            val ctx = LocalContext.current
            LaunchedEffect(Unit) {
                val done = ctx.getSharedPreferences("soleus", android.content.Context.MODE_PRIVATE)
                    .getBoolean("onboarding_done", false)
                if (!done) {
                    navController.navigate(SoleusRotalari.KARSILAMA) {
                        popUpTo(SoleusRotalari.ANA_SAYFA) { inclusive = true }
                    }
                    return@LaunchedEffect
                }
                vm.refresh()
            }
            val siradaki = egzersizler.firstOrNull()
            if (siradaki != null) {
                HomeScreen(
                    nextName = siradaki.trName,
                    nextDurationSec = siradaki.durationSec,
                    streak = streak,
                    onStart = { navController.navigate(SoleusRotalari.detay(siradaki.id)) },
                    onOpenList = { navController.navigate(SoleusRotalari.LISTE) },
                    onOpenStats = { navController.navigate(SoleusRotalari.ISTATISTIK) },
                    onOpenSettings = { navController.navigate(SoleusRotalari.AYARLAR) }
                )
            }
        }
        composable(SoleusRotalari.LISTE) {
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
            val egzersiz = egzersizler.firstOrNull { it.id == id }
                ?: egzersizler.firstOrNull()
            if (egzersiz != null) {
                ExerciseDetailScreen(
                    exercise = egzersiz,
                    onDone = {
                        vm.logCompletion(egzersiz.id, egzersiz.durationSec)
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(SoleusRotalari.ISTATISTIK) {
            val statsVm: StatsViewModel = viewModel()
            val istatistikStreak by statsVm.streak.collectAsState()
            val counts by statsVm.weeklyCounts.collectAsState()
            StatsScreen(
                streak = istatistikStreak,
                weeklyCounts = counts
            )
        }
        composable(SoleusRotalari.AYARLAR) {
            val settingsVm: SettingsViewModel = viewModel()
            val ayar by settingsVm.settings.collectAsState()
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
                    saveError = settingsVm.saveError.collectAsState().value,
                    onSave = { start, end, interval, done ->
                        settingsVm.save(start, end, interval) { done(it) }
                    },
                    onSaved = { navController.popBackStack() }
                )
            }
        }
    }
}
