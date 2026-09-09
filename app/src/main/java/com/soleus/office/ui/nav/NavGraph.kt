package com.soleus.office.ui.nav

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
import com.soleus.office.ui.theme.SereneOutline
import com.soleus.office.ui.theme.SerenePrimary
import com.soleus.office.ui.theme.SerenePrimaryContainer

object SoleusRotalari {
    const val KARSILAMA = "onboarding"
    const val ANA_SAYFA = "home"
    const val LISTE = "list"
    const val DETAY = "detail/{id}"
    const val ISTATISTIK = "stats"
    const val AYARLAR = "settings"

    fun detay(id: String): String = "detail/$id"
}

private data class AltSekme(
    val rota: String,
    val etiket: String,
    val seciliIkon: ImageVector,
    val seciliDegilIkon: ImageVector
)

private val ALT_SEKMELER = listOf(
    AltSekme(
        rota = SoleusRotalari.ANA_SAYFA,
        etiket = "Bugün",
        seciliIkon = Icons.Filled.Home,
        seciliDegilIkon = Icons.Outlined.Home
    ),
    AltSekme(
        rota = SoleusRotalari.LISTE,
        etiket = "Hareketler",
        seciliIkon = Icons.Filled.FormatListBulleted,
        seciliDegilIkon = Icons.Outlined.FormatListBulleted
    ),
    AltSekme(
        rota = SoleusRotalari.ISTATISTIK,
        etiket = "Geçmiş",
        seciliIkon = Icons.Filled.CalendarMonth,
        seciliDegilIkon = Icons.Outlined.CalendarMonth
    ),
    AltSekme(
        rota = SoleusRotalari.AYARLAR,
        etiket = "Ayarlar",
        seciliIkon = Icons.Filled.Settings,
        seciliDegilIkon = Icons.Outlined.Settings
    )
)

private val UST_DUZEY_ROTALAR = ALT_SEKMELER.map { it.rota }.toSet()

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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val mevcutHedef = navBackStackEntry?.destination
    val altBarGoster = mevcutHedef?.route in UST_DUZEY_ROTALAR

    Scaffold(
        bottomBar = {
            if (altBarGoster) {
                NavigationBar(containerColor = Color.White) {
                    ALT_SEKMELER.forEach { sekme ->
                        val secili = mevcutHedef?.hierarchy?.any { it.route == sekme.rota } == true
                        NavigationBarItem(
                            selected = secili,
                            onClick = {
                                navController.navigate(sekme.rota) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (secili) sekme.seciliIkon else sekme.seciliDegilIkon,
                                    contentDescription = sekme.etiket
                                )
                            },
                            label = { Text(text = sekme.etiket) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SerenePrimary,
                                selectedTextColor = SerenePrimary,
                                unselectedIconColor = SereneOutline,
                                unselectedTextColor = SereneOutline,
                                indicatorColor = SerenePrimaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { icPadding ->
        NavHost(
            navController = navController,
            startDestination = SoleusRotalari.ANA_SAYFA,
            modifier = Modifier.padding(icPadding)
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
}
