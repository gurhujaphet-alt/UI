package org.babetech.borastock.ui.navigation


import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController

class AdaptiveNavigatorState(
    private val navController: NavHostController
) : NavigatorState {

    val detailPaneRoute = mutableStateOf<String?>(null)
    val supportingPaneRoute = mutableStateOf<String?>(null)

    override fun navigateTo(route: String) {
        navController.navigate(route)
    }

    override fun navigateToDetailPane(route: String) {
        detailPaneRoute.value = route
    }

    override fun navigateToSupportingPane(route: String) {
        supportingPaneRoute.value = route
    }

    override fun goBack() {
        if (!navController.popBackStack()) {
            // Peut gérer une logique de fallback ici
        }
    }
}
