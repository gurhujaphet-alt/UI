package org.babetech.borastock.ui.screens.auth

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val navigator = rememberSupportingPaneScaffoldNavigator()

    SupportingPaneScaffold(
        value = navigator.scaffoldValue,
        directive = navigator.scaffoldDirective,

        mainPane = {
            AnimatedPane {
                LoginOnboardingScreen(
                    onStartLogin = {
                        navigator.navigateTo(androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole.Supporting)
                    },
                    isLoginVisible = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] != PaneAdaptedValue.Hidden

                )
            }
        },

        supportingPane = {
            AnimatedPane {
                LoginForm(
                    onSubmit = onLoginSuccess,
                    onGoogleSignIn = {
                        // Gérer ici l'authentification Google
                    }
                )
            }
        }
    )
}
