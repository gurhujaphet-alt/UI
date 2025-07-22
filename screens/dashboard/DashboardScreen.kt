package org.babetech.borastock.ui.screens.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import org.babetech.borastock.ui.components.NavigationScaffoldScreen
import org.babetech.borastock.ui.navigation.AppDestinations
import org.babetech.borastock.ui.screens.screennavigation.Entries.EntriesScreen
import org.babetech.borastock.ui.screens.screennavigation.Settings.SettingsScreen
import org.babetech.borastock.ui.screens.screennavigation.Statistique.StatistiqueScreen
import org.babetech.borastock.ui.screens.screennavigation.StockScreen
import org.babetech.borastock.ui.screens.screennavigation.exits.ExitsScreen
import org.babetech.borastock.ui.screens.screennavigation.suppliers.SuppliersScreen

@Composable
fun DashboardScreen(onThemeChange: (String) -> Unit, currentTheme: String){
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.Home) }





        NavigationScaffoldScreen(

            title = "BoraStock",
            onThemeChange = onThemeChange,
            currentDestination = currentDestination,
            onDestinationChanged = { currentDestination = it },
            currentTheme = currentTheme,
        ) {
            when (currentDestination) {
                AppDestinations.Home -> AccueilScreen()
                AppDestinations.Stocks -> StockScreen()
                AppDestinations.Entries -> EntriesScreen()
                AppDestinations.Exits -> ExitsScreen()
                AppDestinations.Suppliers -> SuppliersScreen()
                AppDestinations.Analytics -> StatistiqueScreen()
                AppDestinations.Settings -> SettingsScreen()
            }
        }
    }
