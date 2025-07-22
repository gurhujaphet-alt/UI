package org.babetech.borastock.ui.navigation


import androidx.compose.runtime.Stable

@Stable
interface NavigatorState {
    fun navigateTo(route: String)
    fun navigateToDetailPane(route: String)
    fun navigateToSupportingPane(route: String)
    fun goBack()
}
