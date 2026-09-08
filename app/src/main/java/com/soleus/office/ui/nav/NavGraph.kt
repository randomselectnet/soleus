package com.soleus.office.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.soleus.office.data.ContentLoader
import com.soleus.office.ui.detail.ExerciseDetailScreen
import com.soleus.office.ui.home.HomeScreen
import com.soleus.office.ui.list.ExerciseListScreen

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
    NavHost(
        navController = navController,
        startDestination = SoleusRotalari.ANA_SAYFA
    ) {
        composable(SoleusRotalari.KARSILAMA) {
            YerTutucuEkrani("Hoş geldin")
        }
        composable(SoleusRotalari.ANA_SAYFA) {
            val ctx = LocalContext.current
            val egzersizler = remember { ContentLoader.load(ctx) }
            val siradaki = egzersizler.firstOrNull()
            if (siradaki != null) {
                HomeScreen(
                    nextName = siradaki.trName,
                    nextDurationSec = siradaki.durationSec,
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
        composable(SoleusRotalari.DETAY) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            val ctx = LocalContext.current
            val egzersizler = remember { ContentLoader.load(ctx) }
            val egzersiz = egzersizler.firstOrNull { it.id == id }
                ?: egzersizler.firstOrNull()
            if (egzersiz != null) {
                ExerciseDetailScreen(
                    exercise = egzersiz,
                    onDone = { navController.popBackStack() }
                )
            }
        }
        composable(SoleusRotalari.ISTATISTIK) {
            YerTutucuEkrani("İstatistik")
        }
        composable(SoleusRotalari.AYARLAR) {
            YerTutucuEkrani("Ayarlar")
        }
    }
}

// Geçici yer tutucu; gerçek ekranlar Task 5-6'da bağlanacak.
@Composable
private fun YerTutucuEkrani(baslik: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = baslik,
            style = MaterialTheme.typography.titleLarge
        )
    }
}
