package org.babetech.borastock.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import borastock.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

enum class AppDestinations(
    val label: String,
    val contentDescription: String,
    val icon: @Composable () -> Painter
) {
    Home(
        label = "Accueil",
        contentDescription = "Accueil",
        icon = { painterResource(Res.drawable.Warehouse) }
    ),

    Stocks(
        label = "Stocks",
        contentDescription = "Liste des stocks",
        icon = { painterResource(Res.drawable.inventory) }
    ),

    Entries(
        label = "Entrées",
        contentDescription = "Produits entrants",
        icon = { painterResource(Res.drawable.Entry) }
    ),

    Exits(
        label = "Sorties",
        contentDescription = "Produits sortants",
        icon = { painterResource(Res.drawable.Exit) }
    ),

    Suppliers(
        label = "Fournisseurs",
        contentDescription = "Liste des fournisseurs",
        icon = { painterResource(Res.drawable.Person) }
    ),

    Analytics(
        label = "Statistiques",
        contentDescription = "Analyse des données",
        icon = { painterResource(Res.drawable.analytics) }
    ),

    Settings(
        label = "Paramètres",
        contentDescription = "Réglages",
        icon = { painterResource(Res.drawable.Setting) }
    )
}
