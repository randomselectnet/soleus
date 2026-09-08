package com.soleus.office.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
            YerTutucuEkrani("Bugün")
        }
        composable(SoleusRotalari.LISTE) {
            YerTutucuEkrani("Hareketler")
        }
        composable(SoleusRotalari.DETAY) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            YerTutucuEkrani("Hareket: $id")
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
