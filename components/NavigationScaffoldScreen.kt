package org.babetech.borastock.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import borastock.composeapp.generated.resources.DarkMode
import borastock.composeapp.generated.resources.LightMode
import borastock.composeapp.generated.resources.Res
import org.babetech.borastock.ui.navigation.AppDestinations
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScaffoldScreen(
    title: String,
    currentDestination: AppDestinations,
    onDestinationChanged: (AppDestinations) -> Unit,
    onThemeChange: (String) -> Unit, // Added onThemeChange parameter
    currentTheme: String, // Added onThemeChange parameter
    content: @Composable (() -> Unit)
) {
    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var showNavigationDrawer by rememberSaveable { mutableStateOf(true) }


    BoxWithConstraints {
        val isCompact = maxWidth < 600.dp
        val isMedium = maxWidth in 600.dp..839.dp
        val isExpanded = maxWidth >= 840.dp

        // Determine the navigation layout type based on screen width
        val layoutType = when {
            isExpanded && showNavigationDrawer -> NavigationSuiteType.NavigationDrawer
            isExpanded && !showNavigationDrawer -> NavigationSuiteType.NavigationRail
            isMedium -> NavigationSuiteType.NavigationRail
            else -> NavigationSuiteType.NavigationBar
        }

        // Adjust which destinations to show based on the layout type
        val destinationsToShow = if (layoutType == NavigationSuiteType.NavigationBar)
            AppDestinations.entries.take(5) // Show only first 5 for NavigationBar
        else AppDestinations.entries // Show all for other types

      //  var themeSelection by remember { mutableStateOf("system") }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    },
                    navigationIcon = {
                        // Show menu icon only when expanded for toggling NavigationDrawer
                        if (isExpanded) {
                            IconButton(onClick = { showNavigationDrawer = !showNavigationDrawer }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { isMenuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profil",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mon profil") },
                                onClick = {
                                    isMenuExpanded = false
                                    // TODO: action profil
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Déconnexion") },
                                onClick = {
                                    isMenuExpanded = false
                                    // TODO: action déconnexion
                                }
                            )
                        }

                        // Theme change buttons
                        // These should typically be within a DropdownMenu or a separate settings screen,
                        // but placed here as per original code structure.
                        ThemeSwitcher(
                            onThemeChange = onThemeChange,
                            currentTheme = currentTheme.toString()
                        )
                    }, // Closing parenthesis for actions lambda was missing
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        ) { innerPadding ->
            NavigationSuiteScaffold(
                modifier = Modifier.padding(innerPadding),
                layoutType = layoutType,
                navigationSuiteItems = {
                    destinationsToShow.forEach { dest ->
                        item(
                            selected = currentDestination == dest,
                            onClick = { onDestinationChanged(dest) },
                            icon = {
                                // Assuming AppDestinations has a function `icon()` that returns a Painter
                                Icon(
                                    painter = dest.icon(),
                                    contentDescription = dest.contentDescription,
                                    tint = if (currentDestination == dest)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            label = {
                                // Only show label for non-NavigationBar types to save space
                                if (layoutType != NavigationSuiteType.NavigationBar) {
                                    Text(
                                        text = dest.label,
                                        color = if (currentDestination == dest)
                                            MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        )
                    }
                }
            ) {
                content() // Display the main content of the screen
            }
        }
    }
}

@Composable
fun ThemeSwitcher(onThemeChange: (String) -> Unit, currentTheme: String) {
    Button(
        onClick = {
            // Toggle the theme
            if (currentTheme == "light") {
                onThemeChange("dark")
            } else {
                onThemeChange("light")
            }
        }
    ) {
        // Animate the icon and text change
        Crossfade(targetState = currentTheme, animationSpec = tween(500)) { theme ->

                if (theme == "dark") {
                    Icon(
                        painter = painterResource(Res.drawable.DarkMode),
                        contentDescription = "Thème sombre"
                    )

                } else {
                    Icon(
                        painter = painterResource(Res.drawable.LightMode),
                        contentDescription = "Thème clair"
                    )

                }

        }
    }
}