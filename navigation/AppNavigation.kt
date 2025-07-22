package org.babetech.borastock.ui.navigation


sealed class AppDestination(val route: String) {

    object Onbarding : AppDestination("Onbarding")
    object Login : AppDestination("login")
    object CompanySetup : AppDestination("company_setup")
    object Dashboard : AppDestination("dashboard")

    object ProductList : AppDestination("products")
    object ProductDetail : AppDestination("products/detail")

    object SupplierList : AppDestination("suppliers")
    object SupplierDetail : AppDestination("suppliers/detail")
}
