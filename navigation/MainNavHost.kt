package org.babetech.borastock.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.babetech.borastock.ui.navigation.AppDestination.CompanySetup
import org.babetech.borastock.ui.navigation.AppDestination.Dashboard
import org.babetech.borastock.ui.navigation.AppDestination.Login
import org.babetech.borastock.ui.navigation.AppDestination.ProductDetail
import org.babetech.borastock.ui.navigation.AppDestination.ProductList
import org.babetech.borastock.ui.navigation.AppDestination.SupplierDetail
import org.babetech.borastock.ui.navigation.AppDestination.SupplierList
import org.babetech.borastock.ui.screens.auth.LoginScreen
import org.babetech.borastock.ui.screens.dashboard.DashboardScreen
import org.babetech.borastock.ui.screens.onbardingScreen.OnboardingScreen
import org.babetech.borastock.ui.screens.products.ProductListScreen
import org.babetech.borastock.ui.screens.setup.CompanySetupScreen
import org.babetech.borastock.ui.screens.suppliers.SupplierListScreen


@Composable
fun MainNavHost(
    themeSelection: String,
    onThemeChange: (String) -> Unit,
) {
    val navController = rememberNavController()  // <-- extraire ici

    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onContinueClicked = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable(Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Dashboard.route) }
            )
        }

        composable(CompanySetup.route) {
            CompanySetupScreen(
                // onFinish = { navController.navigate(Dashboard.route) }
            )
        }

        composable(Dashboard.route) {
            DashboardScreen(
                onThemeChange = onThemeChange,
                currentTheme = themeSelection
                // onNavigateToProducts = { navController.navigate(ProductList.route) },
                // onNavigateToSuppliers = { navController.navigate(SupplierList.route) }
            )
        }

        composable(ProductList.route) {
            ProductListScreen(
                // onProductSelected = { productId -> navController.navigate(ProductDetail.route + "/$productId") }
            )
        }
        composable(ProductDetail.route + "/{productId}") {
            val productId = it.arguments?.getString("productId")
            // ProductDetailScreen(productId = productId)
        }

        composable(SupplierList.route) {
            SupplierListScreen(
                // onSupplierSelected = { supplierId -> navController.navigate(SupplierDetail.route + "/$supplierId") }
            )
        }
        composable(SupplierDetail.route + "/{supplierId}") {
            val supplierId = it.arguments?.getString("supplierId")
            // SupplierDetailScreen(supplierId = supplierId)
        }
    }
}