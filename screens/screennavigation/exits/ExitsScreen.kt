package org.babetech.borastock.ui.screens.screennavigation.exits

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.*
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import borastock.composeapp.generated.resources.LocalShipping
import borastock.composeapp.generated.resources.Output
import borastock.composeapp.generated.resources.PlayArrow
import borastock.composeapp.generated.resources.PriorityHigh
import borastock.composeapp.generated.resources.Receipt
import borastock.composeapp.generated.resources.Refresh
import borastock.composeapp.generated.resources.Remove
import borastock.composeapp.generated.resources.Res
import borastock.composeapp.generated.resources.Schedule
import borastock.composeapp.generated.resources.TrendingDown
import borastock.composeapp.generated.resources.Warning
import borastock.composeapp.generated.resources.ic_cancel_filled
import borastock.composeapp.generated.resources.ic_check_circle
import borastock.composeapp.generated.resources.inventory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.babetech.borastock.ui.screens.screennavigation.Entries.StockHeader
import org.babetech.borastock.ui.screens.screennavigation.Entries.StockStat
import org.babetech.borastock.ui.screens.screennavigation.Entries.StockSummary
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


data class StockExit(
    val id: String,
    val productName: String,
    val category: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalValue: Double,
    val customer: String,
    val exitDate: LocalDateTime,
    val orderNumber: String?,
    val deliveryAddress: String?,
    val status: ExitStatus,
    val notes: String?,
    val urgency: ExitUrgency
)

enum class ExitStatus(val label: String, val color: Color, val iconRes: DrawableResource) {
    PENDING("En préparation", Color(0xFFf59e0b), Res.drawable.Schedule),
    PREPARED("Préparée", Color(0xFF3b82f6),  Res.drawable.inventory),
    SHIPPED("Expédiée", Color(0xFF8b5cf6), Res.drawable.LocalShipping),
    DELIVERED("Livrée", Color(0xFF22c55e), Res.drawable.ic_check_circle),
    CANCELLED("Annulée", Color(0xFFef4444), Res.drawable.ic_cancel_filled)
}

enum class ExitUrgency(val label: String, val color: Color, val iconRes: DrawableResource) {
    LOW("Normale", Color(0xFF6b7280), Res.drawable.Remove),
    MEDIUM("Prioritaire", Color(0xFFf59e0b), Res.drawable.PriorityHigh),
    HIGH("Urgente", Color(0xFFef4444),Res.drawable.Warning)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalTime::class)
@Composable
fun ExitsScreen() {
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    var selectedExit by remember { mutableStateOf<StockExit?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Toutes") }
    var sortBy by remember { mutableStateOf("Date") }
    var isLoading by remember { mutableStateOf(true) }


    val timeZone = TimeZone.currentSystemDefault()

    val nowInstant = Clock.System.now()



    val now = Clock.System.now()

    fun nowMinus(days: Int = 0, hours: Int = 0): LocalDateTime {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now()
        val period = DateTimePeriod(days = days, hours = hours)
        return now.minus(period, timeZone).toLocalDateTime(timeZone)
    }



    val period = DateTimePeriod(days = 1, hours = 6)
    val exitDate = now.minus(period, timeZone).toLocalDateTime(timeZone)



    val instantNow = Clock.System.now()
    fun nowMinusHours(hours: Int): LocalDateTime {
        return Clock.System.now()
            .minus(hours, DateTimeUnit.HOUR)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }


    // Animation de chargement initial
    LaunchedEffect(Unit) {
        delay(800)
        isLoading = false
    }

    // Données d'exemple avec différents statuts et urgences
    val stockExits = remember {
        listOf(
            StockExit(
                id = "S001",
                productName = "iPhone 15 Pro Max",
                category = "Électronique",
                quantity = 2,
                unitPrice = 1199.99,
                totalValue = 2399.98,
                customer = "TechStore Paris",
                exitDate = nowMinusHours(1),
                orderNumber = "CMD2024001",
                deliveryAddress = "123 Rue de Rivoli, 75001 Paris",
                status = ExitStatus.SHIPPED,
                notes = "Livraison express demandée",
                urgency = ExitUrgency.HIGH
            ),
            StockExit(
                id = "S002",
                productName = "Samsung Galaxy S24 Ultra",
                category = "Électronique",
                quantity = 1,
                unitPrice = 1299.99,
                totalValue = 1299.99,
                customer = "Mobile World Lyon",
                exitDate = nowMinusHours(3),
                orderNumber = "CMD2024002",
                deliveryAddress = "45 Place Bellecour, 69002 Lyon",
                status = ExitStatus.DELIVERED,
                notes = "Client satisfait, livraison réussie",
                urgency = ExitUrgency.LOW
            ),
            StockExit(
                id = "S003",
                productName = "MacBook Air M3",
                category = "Informatique",
                quantity = 3,
                unitPrice = 1299.99,
                totalValue = 3899.97,
                customer = "Université de Bordeaux",
                exitDate = nowMinusHours(5),
                orderNumber = "CMD2024003",
                deliveryAddress = "351 Cours de la Libération, 33405 Talence",
                status = ExitStatus.PREPARED,
                notes = "Commande institutionnelle, facture séparée",
                urgency = ExitUrgency.MEDIUM
            ),
            StockExit(
                id = "S004",
                productName = "AirPods Pro 2",
                category = "Audio",
                quantity = 10,
                unitPrice = 279.99,
                totalValue = 2799.90,
                customer = "AudioMax Marseille",
                exitDate = nowMinusHours(8),
                orderNumber = "CMD2024004",
                deliveryAddress = "12 La Canebière, 13001 Marseille",
                status = ExitStatus.PENDING,
                notes = "Vérifier stock avant expédition",
                urgency = ExitUrgency.LOW
            ),
            StockExit(
                id = "S005",
                productName = "Dell XPS 13",
                category = "Informatique",
                quantity = 1,
                unitPrice = 999.99,
                totalValue = 999.99,
                customer = "StartupTech Lille",
                exitDate = nowMinusHours(12),
                orderNumber = "CMD2024005",
                deliveryAddress = "78 Rue Nationale, 59000 Lille",
                status = ExitStatus.CANCELLED,
                notes = "Annulée - problème de paiement",
                urgency = ExitUrgency.LOW
            ),
            StockExit(
                id = "S006",
                productName = "Sony WH-1000XM5",
                category = "Audio",
                quantity = 5,
                unitPrice = 399.99,
                totalValue = 1999.95,
                customer = "MusicStore Toulouse",
                exitDate = nowMinusHours(1),
                orderNumber = "CMD2024006",
                deliveryAddress = "25 Place du Capitole, 31000 Toulouse",
                status = ExitStatus.DELIVERED,
                notes = "Livraison parfaite, client régulier",
                urgency = ExitUrgency.MEDIUM
            ),
            StockExit(
                id = "S007",
                productName = "iPad Pro 12.9",
                category = "Tablettes",
                quantity = 2,
                unitPrice = 1099.99,
                totalValue = 2199.98,
                customer = "DesignStudio Nice",
                exitDate = nowMinus(days = 1, hours = 6),
                orderNumber = "CMD2024007",
                deliveryAddress = "10 Promenade des Anglais, 06000 Nice",
                status = ExitStatus.SHIPPED,
                notes = "Matériel professionnel pour studio",
                urgency = ExitUrgency.HIGH
            )
        )
    }

    val filteredExits = stockExits.filter { exit ->
        val matchesSearch = exit.productName.contains(searchQuery, ignoreCase = true) ||
                exit.category.contains(searchQuery, ignoreCase = true) ||
                exit.customer.contains(searchQuery, ignoreCase = true) ||
                exit.orderNumber?.contains(searchQuery, ignoreCase = true) == true

        val matchesFilter = when (selectedFilter) {
            "Toutes" -> true
            "En préparation" -> exit.status == ExitStatus.PENDING
            "Préparées" -> exit.status == ExitStatus.PREPARED
            "Expédiées" -> exit.status == ExitStatus.SHIPPED
            "Livrées" -> exit.status == ExitStatus.DELIVERED
            "Annulées" -> exit.status == ExitStatus.CANCELLED
            else -> true
        }

        matchesSearch && matchesFilter
    }.let { exits ->
        when (sortBy) {
            "Date" -> exits.sortedByDescending { it.exitDate }
            "Produit" -> exits.sortedBy { it.productName }
            "Client" -> exits.sortedBy { it.customer }
            "Quantité" -> exits.sortedByDescending { it.quantity }
            "Valeur" -> exits.sortedByDescending { it.totalValue }
            "Statut" -> exits.sortedBy { it.status.label }
            "Urgence" -> exits.sortedByDescending { it.urgency.ordinal }
            else -> exits
        }
    }

    SupportingPaneScaffold(
        value = navigator.scaffoldValue,
        directive = navigator.scaffoldDirective,
        mainPane = {
            AnimatedPane {
                ExitsMainPane(
                    exits = filteredExits,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it },
                    sortBy = sortBy,
                    onSortChange = { sortBy = it },
                    onExitSelected = { exit ->
                        selectedExit = exit
                        scope.launch {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                        }
                    },
                    isLoading = isLoading
                )
            }
        },
        supportingPane = {
            AnimatedPane {
                selectedExit?.let { exit ->
                    ExitDetailPane(
                        exit = exit,
                        onBack = {
                            scope.launch { navigator.navigateBack() }
                        },
                        showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Main] == PaneAdaptedValue.Hidden
                    )
                }
            }
        }
    )
}


@Composable
private fun ExitsMainPane(
    exits: List<StockExit>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    sortBy: String,
    onSortChange: (String) -> Unit,
    onExitSelected: (StockExit) -> Unit,
    isLoading: Boolean
) {
    var headerVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "fab_pulse")
    val fabScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fab_scale"
    )

    LaunchedEffect(Unit) {
        delay(200)
        headerVisible = true
        delay(300)
        contentVisible = true
    }

    LazyColumn( // Changed from Column to LazyColumn
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f)
                    )
                )
            ),
        contentPadding = PaddingValues(vertical = 12.dp), // Add overall content padding
        verticalArrangement = Arrangement.spacedBy(10.dp) // Consistent spacing between items
    ) {
        // Header with entry animation
        item {
            AnimatedVisibility(
                visible = headerVisible,
                enter = slideInVertically(
                    animationSpec = tween(600, easing = EaseOutCubic),
                    initialOffsetY = { -it }
                ) + fadeIn(animationSpec = tween(600))
            ) {
                StockHeader(
                    title = "Sorties de Stock",
                    subtitle = "Gestion des expéditions et livraisons",
                    icon = painterResource(Res.drawable.Output),
                    iconColor = MaterialTheme.colorScheme.primary,
                    stats = listOf(
                        StockStat("Total Sorties", "25", Res.drawable.Receipt, MaterialTheme.colorScheme.primary),
                        StockStat("En Préparation", "8", Res.drawable.Schedule, Color(0xFFf59e0b)),
                        StockStat("Expédiées", "12", Res.drawable.LocalShipping, Color(0xFF8b5cf6)),
                        StockStat("Livrées", "5", Res.drawable.ic_check_circle, Color(0xFF22c55e))
                    ),
                    summaries = listOf(
                        StockSummary(
                            "Valeur totale du stock",
                            "25000 €",
                            Res.drawable.TrendingDown,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.primary
                        ),
                        StockSummary(
                            "Sorties urgentes",
                            "3",
                            Res.drawable.Warning,
                            Color(0xFFef4444),
                            Color(0xFFef4444).copy(alpha = 0.1f),
                            Color(0xFFef4444)
                        )
                    )
                )

            }
        }

        // Search and filters section with animation
        item {
            AnimatedVisibility(
                visible = contentVisible,
                enter = slideInVertically(
                    animationSpec = tween(700, delayMillis = 200, easing = EaseOutCubic),
                    initialOffsetY = { it / 2 }
                ) + fadeIn(animationSpec = tween(700, delayMillis = 200))
            ) {
                SearchAndFiltersSection(
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    selectedFilter = selectedFilter,
                    onFilterChange = onFilterChange,
                    sortBy = sortBy,
                    onSortChange = onSortChange
                )
            }
        }

        // List of exits with loading animation
        if (isLoading) {
            item {
                LoadingAnimation(modifier = Modifier.fillParentMaxHeight())
            }
        } else {
            items(exits) { exit -> // Directly use items here
                var visible by remember { mutableStateOf(false) }

                LaunchedEffect(exit.id) {
                    delay(50)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally(
                        animationSpec = tween(500, easing = EaseOutCubic),
                        initialOffsetX = { it }
                    ) + fadeIn(animationSpec = tween(500))
                ) {
                    ExitCard(
                        exit = exit,
                        onClick = { onExitSelected(exit) }
                    )
                }
            }
        }

        // FAB with bounce and pulse animation
        item {
            AnimatedVisibility(
                visible = contentVisible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    initialScale = 0f
                ) + fadeIn(animationSpec = tween(500, delayMillis = 600))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp), // Adjusted padding for FAB
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = { /* TODO: Ajouter nouvelle sortie */ },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.shadow(
                            elevation = 8.dp, // Adjusted elevation
                            shape = RoundedCornerShape(16.dp)
                        )
                            .graphicsLayer {
                                scaleX = fabScale
                                scaleY = fabScale
                            }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Ajouter une sortie",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun LoadingAnimation(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "loading")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ), label = "loading_rotation"
            )

            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .graphicsLayer { rotationZ = rotation },
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                "Chargement des sorties...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAndFiltersSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    sortBy: String,
    onSortChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp) // Réduction du padding
            .shadow(
                elevation = 2.dp, // Réduction de l'élévation
                shape = RoundedCornerShape(8.dp) // Réduction du rayon des coins
            ),
        shape = RoundedCornerShape(8.dp), // Réduction du rayon des coins
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp), // Réduction du padding
            verticalArrangement = Arrangement.spacedBy(8.dp) // Réduction de l'espacement
        ) {
            // Barre de recherche avec animation
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Rechercher une sortie...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp), // Réduction du rayon des coins
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // Filtres et tri
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp) // Réduction de l'espacement
            ) {
                // Filtre par statut
                var filterExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = filterExpanded,
                    onExpandedChange = { filterExpanded = !filterExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedFilter,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrer") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp) // Réduction du rayon des coins
                    )
                    ExposedDropdownMenu(
                        expanded = filterExpanded,
                        onDismissRequest = { filterExpanded = false }
                    ) {
                        listOf("Toutes", "En préparation", "Préparées", "Expédiées", "Livrées", "Annulées").forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter) },
                                onClick = {
                                    onFilterChange(filter)
                                    filterExpanded = false
                                }
                            )
                        }
                    }
                }

                // Tri
                var sortExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = sortExpanded,
                    onExpandedChange = { sortExpanded = !sortExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = sortBy,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Trier par") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp) // Réduction du rayon des coins
                    )
                    ExposedDropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false }
                    ) {
                        listOf("Date", "Produit", "Client", "Quantité", "Valeur", "Statut", "Urgence").forEach { sort ->
                            DropdownMenuItem(
                                text = { Text(sort) },
                                onClick = {
                                    onSortChange(sort)
                                    sortExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExitsList(
    exits: List<StockExit>,
    onExitSelected: (StockExit) -> Unit,
    modifier: Modifier = Modifier
) {
    // ExitsList est maintenant un simple wrapper qui ne contient plus de LazyColumn
    // La LazyColumn est maintenant dans ExitsMainPane
//    Column(
//        modifier = modifier.padding(horizontal = 16.dp), // Réduction du padding horizontal
//        verticalArrangement = Arrangement.spacedBy(10.dp), // Réduction de l'espacement
//        contentPadding = PaddingValues(vertical = 12.dp) // Réduction du padding vertical
//    ) {
//        // Le contenu de la liste est maintenant directement géré par le LazyColumn dans ExitsMainPane
//        // Ce composable n'est plus utilisé comme un LazyColumn, mais comme un simple conteneur pour les ExitCard
//        // Les items sont passés directement au LazyColumn parent dans ExitsMainPane
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExitCard(
    exit: StockExit,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isPressed) 4.dp else 2.dp, // Réduction de l'élévation
                shape = RoundedCornerShape(12.dp), // Réduction du rayon des coins
                spotColor = exit.status.color.copy(alpha = 0.15f)
            )
            .graphicsLayer {
                scaleX = if (isPressed) 0.98f else 1f
                scaleY = if (isPressed) 0.98f else 1f
            },
        shape = RoundedCornerShape(12.dp), // Réduction du rayon des coins
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(100)
                isPressed = false
            }
        }

        Column(
            modifier = Modifier.padding(16.dp), // Réduction du padding
            verticalArrangement = Arrangement.spacedBy(10.dp) // Réduction de l'espacement
        ) {
            // En-tête avec nom, statut et urgence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exit.productName,
                        style = MaterialTheme.typography.titleMedium.copy( // Ajustement de la taille de la police
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = exit.category,
                        style = MaterialTheme.typography.bodySmall.copy( // Ajustement de la taille de la police
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp) // Réduction de l'espacement
                ) {
                    // Badge de statut
                    Card(
                        shape = RoundedCornerShape(16.dp), // Réduction du rayon des coins
                        colors = CardDefaults.cardColors(
                            containerColor = exit.status.color.copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), // Réduction du padding
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp) // Réduction de l'espacement
                        ) {
                            Icon(
                                painter = painterResource(exit.status.iconRes),
                                contentDescription = null,
                                tint = exit.status.color,
                                modifier = Modifier.size(12.dp) // Réduction de la taille
                            )
                            Text(
                                text = exit.status.label,
                                style = MaterialTheme.typography.labelSmall.copy( // Ajustement de la taille de la police
                                    fontWeight = FontWeight.Bold
                                ),
                                color = exit.status.color
                            )
                        }
                    }

                    // Badge d'urgence si nécessaire
                    if (exit.urgency != ExitUrgency.LOW) {
                        Card(
                            shape = RoundedCornerShape(16.dp), // Réduction du rayon des coins
                            colors = CardDefaults.cardColors(
                                containerColor = exit.urgency.color.copy(alpha = 0.15f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), // Réduction du padding
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp) // Réduction de l'espacement
                            ) {
                                Icon(
                                    painter = painterResource(exit.urgency.iconRes),
                                    contentDescription = null,
                                    tint = exit.urgency.color,
                                    modifier = Modifier.size(10.dp) // Réduction de la taille
                                )
                                Text(
                                    text = exit.urgency.label,
                                    style = MaterialTheme.typography.labelSmall.copy( // Ajustement de la taille de la police
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = exit.urgency.color
                                )
                            }
                        }
                    }
                }
            }

            // Informations détaillées
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Réduction de l'espacement
            ) {
                InfoItem(
                    label = "Quantité",
                    value = "${exit.quantity} unités",
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Valeur totale",
                    value = "${exit.totalValue} €",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Réduction de l'espacement
            ) {
                InfoItem(
                    label = "Client",
                    value = exit.customer,
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Date de sortie",
                    value = exit.exitDate.formatAsDateTime(),
                    modifier = Modifier.weight(1f)
                )
            }

            // Commande et adresse
            exit.orderNumber?.let { orderNumber ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp) // Réduction de l'espacement
                ) {
                    InfoItem(
                        label = "N° de commande",
                        value = orderNumber,
                        modifier = Modifier.weight(1f)
                    )
                    exit.deliveryAddress?.let { address ->
                        InfoItem(
                            label = "Adresse de livraison",
                            value = address.split(",").first(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp) // Réduction de l'espacement
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy( // Ajustement de la taille de la police
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy( // Ajustement de la taille de la police
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ExitDetailPane(
    exit: StockExit,
    onBack: () -> Unit,
    showBackButton: Boolean
) {
    var detailVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        detailVisible = true
    }

    // Animation pour la barre de progression de la quantité (si applicable)
    // Nous allons simuler une barre de progression pour la quantité totale des sorties
    // Pour cet exemple, nous allons utiliser une valeur arbitraire de "maxQuantity" pour la démo.
    // Dans une application réelle, cela pourrait être le stock total disponible ou une autre métrique.
    val maxQuantityDemo = 100 // Valeur maximale arbitraire pour la démo
    val animatedQuantityProgress by animateFloatAsState(
        targetValue = (exit.quantity.toFloat() / maxQuantityDemo.toFloat()).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000) // Durée de l'animation en ms
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp), // Réduction du padding
        verticalArrangement = Arrangement.spacedBy(10.dp) // Réduction de l'espacement
    ) {
        if (showBackButton) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp)) // Réduction du rayon des coins
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }
        }

        AnimatedVisibility(
            visible = detailVisible,
            enter = slideInVertically(
                animationSpec = tween(600, easing = EaseOutCubic),
                initialOffsetY = { it / 2 }
            ) + fadeIn(animationSpec = tween(600))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp, // Réduction de l'élévation
                        shape = RoundedCornerShape(16.dp) // Réduction du rayon des coins
                    ),
                shape = RoundedCornerShape(16.dp), // Réduction du rayon des coins
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp), // Réduction du padding
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Réduction de l'espacement
                ) {
                    // En-tête avec animation
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp) // Réduction de l'espacement
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp) // Réduction de la taille
                                .clip(RoundedCornerShape(10.dp)) // Réduction du rayon des coins
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            exit.status.color,
                                            exit.status.color.copy(alpha = 0.7f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(exit.status.iconRes),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp) // Réduction de la taille
                            )
                        }

                        Column {
                            Text(
                                text = exit.productName,
                                style = MaterialTheme.typography.titleLarge.copy( // Ajustement de la taille de la police
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Sortie ${exit.id}",
                                style = MaterialTheme.typography.bodyMedium.copy( // Ajustement de la taille de la police
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )

                    // Détails complets avec sections
                    DetailSection(
                        title = "Informations produit",
                        items = listOf(
                            "Catégorie" to exit.category,
                            "Quantité" to "${exit.quantity} unités",
                            "Prix unitaire" to "${exit.unitPrice} €",
                            "Valeur totale" to "${exit.totalValue} €"
                        )
                    )

                    // Barre de progression pour la quantité
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Quantité de sortie (sur max. ${maxQuantityDemo})",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LinearProgressIndicator(
                            progress = { animatedQuantityProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp) // Réduction de la hauteur
                                .clip(RoundedCornerShape(3.dp)), // Réduction du rayon des coins
                            color = MaterialTheme.colorScheme.primary, // Couleur de la barre de progression
                            trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                        Text(
                            text = "${exit.quantity} unités",
                            style = MaterialTheme.typography.bodySmall.copy( // Ajustement de la taille de la police
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }


                    DetailSection(
                        title = "Informations client",
                        items = listOfNotNull(
                            "Client" to exit.customer,
                            exit.orderNumber?.let { "N° de commande" to it },
                            exit.deliveryAddress?.let { "Adresse de livraison" to it },
                            "Date de sortie" to exit.exitDate.formatAsDateTime()
                        )
                    )



                    DetailSection(
                        title = "Statut et priorité",
                        items = listOf(
                            "Statut" to exit.status.label,
                            "Urgence" to exit.urgency.label
                        )
                    )

                    exit.notes?.let { notes ->
                        DetailSection(
                            title = "Notes",
                            items = listOf("Commentaires" to notes)
                        )
                    }

                    // Actions avec animations
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp) // Réduction de l'espacement
                    ) {
                        OutlinedButton(
                            onClick = { /* TODO: Modifier la sortie */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp), // Réduction du rayon des coins
                            contentPadding = PaddingValues(10.dp) // Réduction du padding
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Modifier", style = MaterialTheme.typography.labelLarge) // Ajustement de la taille de la police
                        }

                        Button(
                            onClick = { /* TODO: Changer le statut */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp), // Réduction du rayon des coins
                            colors = ButtonDefaults.buttonColors(
                                containerColor = exit.status.color
                            )
                        ) {
                            Icon(
                                when (exit.status) {
                                    ExitStatus.PENDING -> painterResource(Res.drawable.PlayArrow)
                                    ExitStatus.PREPARED ->painterResource(Res.drawable.LocalShipping)
                                    ExitStatus.SHIPPED -> painterResource(Res.drawable.ic_check_circle)
                                    else ->painterResource(Res.drawable.Refresh)
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                when (exit.status) {
                                    ExitStatus.PENDING -> "Préparer"
                                    ExitStatus.PREPARED -> "Expédier"
                                    ExitStatus.SHIPPED -> "Confirmer"
                                    else -> "Réactiver"
                                },
                                style = MaterialTheme.typography.labelLarge // Ajustement de la taille de la police
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    items: List<Pair<String, String>>
) {
    var sectionVisible by remember { mutableStateOf(false) }

    LaunchedEffect(title) {
        delay(100)
        sectionVisible = true
    }

    AnimatedVisibility(
        visible = sectionVisible,
        enter = slideInVertically(
            animationSpec = tween(400, easing = EaseOutCubic),
            initialOffsetY = { it / 4 }
        ) + fadeIn(animationSpec = tween(400))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp) // Réduction de l'espacement
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy( // Ajustement de la taille de la police
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy( // Ajustement de la taille de la police
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodySmall.copy( // Ajustement de la taille de la police
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1.5f)
                    )
                }
            }
        }
    }
}



@Composable
fun StatCard(
    title: String,
    value: String,
    icon: Painter,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = color.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),


            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


fun LocalDateTime.formatAsDateTime(): String {
    return "${dayOfMonth.toString().padStart(2, '0')}/" +
            "${monthNumber.toString().padStart(2, '0')}/" +
            "$year " +
            "${hour.toString().padStart(2, '0')}:" +
            "${minute.toString().padStart(2, '0')}"
}
