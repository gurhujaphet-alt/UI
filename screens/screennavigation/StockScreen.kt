package org.babetech.borastock.ui.screens.screennavigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState // Import added for scroll state
import androidx.compose.foundation.verticalScroll // Import added for vertical scroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import borastock.composeapp.generated.resources.CheckCircle
import borastock.composeapp.generated.resources.Error
import borastock.composeapp.generated.resources.Euro
import borastock.composeapp.generated.resources.Res
import borastock.composeapp.generated.resources.TrendingUp
import borastock.composeapp.generated.resources.Visible
import borastock.composeapp.generated.resources.Warning
import borastock.composeapp.generated.resources.inventory
import kotlinx.coroutines.launch
import org.babetech.borastock.ui.screens.screennavigation.exits.StatCard
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

// Définition des classes de données et des enums, inchangées par rapport au code original
data class StockItem(
    val id: String,
    val name: String,
    val category: String,
    val currentStock: Int,
    val minStock: Int,
    val maxStock: Int,
    val price: Double,
    val supplier: String,
    val lastUpdate: String,
    val status: StockStatus
)

enum class StockStatus(val label: String, val color: Color, val icon: DrawableResource) {
    IN_STOCK("En stock", Color(0xFF22c55e), Res.drawable.CheckCircle),
    LOW_STOCK("Stock faible", Color(0xFFf59e0b), Res.drawable.Error), // Changed icon to Error for low stock
    OUT_OF_STOCK("Rupture", Color(0xFFef4444), Res.drawable.TrendingUp), // Changed icon to TrendingUp for out of stock
    OVERSTOCKED("Surstock", Color(0xFF3b82f6), Res.drawable.TrendingUp) // Changed icon to TrendingUp for overstocked
}

/**
 * Composable principal qui utilise `SupportingPaneScaffold` pour une mise en page adaptative.
 * Il affiche la liste des produits à gauche (mainPane) et les détails du produit sélectionné à droite (supportingPane).
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StockScreen() {
    // État pour la recherche, les filtres et le tri, gérés au niveau supérieur
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tous") }
    var sortBy by remember { mutableStateOf("Nom") }

    // Données d'exemple pour les produits en stock
    val stockItems = remember {
        listOf(
            StockItem("1", "iPhone 15 Pro", "Électronique", 25, 10, 100, 1199.99, "Apple Inc.", "Il y a 2h", StockStatus.IN_STOCK),
            StockItem("2", "Samsung Galaxy S24", "Électronique", 8, 15, 80, 899.99, "Samsung", "Il y a 1h", StockStatus.LOW_STOCK),
            StockItem("3", "MacBook Air M3", "Informatique", 0, 5, 50, 1299.99, "Apple Inc.", "Il y a 30min", StockStatus.OUT_OF_STOCK),
            StockItem("4", "AirPods Pro", "Accessoires", 150, 20, 200, 249.99, "Apple Inc.", "Il y a 3h", StockStatus.OVERSTOCKED),
            StockItem("5", "Dell XPS 13", "Informatique", 12, 8, 40, 999.99, "Dell", "Il y a 1h", StockStatus.IN_STOCK),
            StockItem("6", "Sony WH-1000XM5", "Audio", 5, 10, 60, 399.99, "Sony", "Il y a 45min", StockStatus.LOW_STOCK),
            StockItem("7", "iPad Pro 12.9", "Tablettes", 18, 12, 70, 1099.99, "Apple Inc.", "Il y a 2h", StockStatus.IN_STOCK),
            StockItem("8", "Surface Pro 9", "Tablettes", 0, 6, 35, 1199.99, "Microsoft", "Il y a 15min", StockStatus.OUT_OF_STOCK)
        )
    }

    // Filtrage et tri des éléments en fonction des états de recherche et de filtre
    val filteredItems = stockItems.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true) ||
                item.supplier.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Tous" -> true
            "En stock" -> item.status == StockStatus.IN_STOCK
            "Stock faible" -> item.status == StockStatus.LOW_STOCK
            "Rupture" -> item.status == StockStatus.OUT_OF_STOCK
            "Surstock" -> item.status == StockStatus.OVERSTOCKED
            else -> true
        }
        matchesSearch && matchesFilter
    }.let { items ->
        when (sortBy) {
            "Nom" -> items.sortedBy { it.name }
            "Stock" -> items.sortedBy { it.currentStock }
            "Prix" -> items.sortedBy { it.price }
            "Statut" -> items.sortedBy { it.status.label }
            else -> items
        }
    }

    // État pour l'élément de stock actuellement sélectionné
    var selectedStockItem by remember { mutableStateOf<StockItem?>(null) }

    // Navigateur pour le SupportingPaneScaffold
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    // Effet pour sélectionner le premier élément si la liste filtrée n'est pas vide
    // et qu'aucun élément n'est sélectionné. Utile pour les grands écrans.
    LaunchedEffect(filteredItems) {
        if (selectedStockItem == null && filteredItems.isNotEmpty()) {
            selectedStockItem = filteredItems.first()
        }
    }

    SupportingPaneScaffold(
        value = navigator.scaffoldValue,
        directive = navigator.scaffoldDirective,
        mainPane = {
            AnimatedPane {
                // Le LazyColumn gère son propre défilement interne,
                // donc le Column parent ne doit pas être scrollable verticalement.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f)
                                )
                            )
                        )
                ) {
                    // En-tête avec statistiques ajouté comme un item dans LazyColumn
                    item {
                        StockHeader(stockItems = stockItems)
                    }

                    // Barre de recherche et filtres ajoutée comme un item dans LazyColumn
                    item {
                        SearchAndFiltersSection(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            selectedFilter = selectedFilter,
                            onFilterChange = { selectedFilter = it },
                            sortBy = sortBy,
                            onSortChange = { sortBy = it }
                        )
                    }

                    // Liste des produits. Le clic sur un élément met à jour `selectedStockItem`
                    // et navigue pour afficher le volet de support.
                    items(filteredItems) { item ->
                        StockItemCard(item = item, onClick = {
                            selectedStockItem = item
                            scope.launch {
                                navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                            }
                        })
                    }
                }
            }
        },
        supportingPane = {
            AnimatedPane {
                // Affiche les détails du produit sélectionné, ou un message si aucun n'est sélectionné
                selectedStockItem?.let { item ->
                    StockItemDetailScreen(
                        item = item,
                        onBackClick = {
                            scope.launch {
                                navigator.navigateBack()
                            }
                        },
                        // Le bouton retour est visible si le volet de support n'est pas en mode "Expanded" (côte à côte)
                        showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] != PaneAdaptedValue.Expanded
                    )
                } ?: run {
                    // Message de placeholder si aucun élément n'est sélectionné
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sélectionnez un produit pour voir les détails",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )
}

/**
 * Composable pour afficher les détails complets d'un StockItem.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockItemDetailScreen(
    item: StockItem,
    onBackClick: () -> Unit,
    showBackButton: Boolean
) {
    val scrollState = rememberScrollState()

    // Progress animée
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(item.currentStock, item.maxStock) {
        progress = (item.currentStock.toFloat() / item.maxStock.toFloat()).coerceIn(0f, 1f)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "Stock Progress Animation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.name) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Titre
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Catégorie et statut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Catégorie: ${item.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = item.status.color.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            painter = painterResource(item.status.icon),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = item.status.color
                        )
                        Text(
                            text = item.status.label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = item.status.color
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Divider()
            Spacer(modifier = Modifier.height(6.dp))

            // Infos détaillées
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoItem(label = "Stock actuel", value = "${item.currentStock} unités")
                InfoItem(label = "Stock minimum", value = "${item.minStock} unités")
                InfoItem(label = "Stock maximum", value = "${item.maxStock} unités")
                InfoItem(label = "Prix unitaire", value = "${item.price} €")
                InfoItem(label = "Fournisseur", value = item.supplier)
                InfoItem(label = "Dernière mise à jour", value = item.lastUpdate)
                InfoItem(label = "ID Produit", value = item.id)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            // Progression du stock
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Niveau de stock",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.currentStock}/${item.maxStock}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = when {
                        item.currentStock <= item.minStock -> Color(0xFFef4444)
                        item.currentStock >= item.maxStock * 0.8 -> Color(0xFF3b82f6)
                        else -> Color(0xFF22c55e)
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { /* TODO: Modifier stock */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Modifier", style = MaterialTheme.typography.labelLarge)
                }

                Button(
                    onClick = { /* TODO: Historique */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    Icon(painterResource(Res.drawable.Visible), contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Historique", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}


/**
 * Composable pour l'en-tête de l'écran de stock, affichant les statistiques globales.
 */
@Composable
fun StockHeader(stockItems: List<StockItem>) {
    val totalItems = stockItems.size
    val inStock = stockItems.count { it.status == StockStatus.IN_STOCK }
    val lowStock = stockItems.count { it.status == StockStatus.LOW_STOCK }
    val outOfStock = stockItems.count { it.status == StockStatus.OUT_OF_STOCK }
    val totalValue = stockItems.sumOf { it.price * it.currentStock }








    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Titre
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                            )
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.inventory),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Gestion des Stocks",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Vue d'overview de votre inventaire",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Statistiques
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min), // 👈 égalise la hauteur des cartes
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StatCard(
                    title = "Total Produits",
                    value = totalItems.toString(),
                    icon = painterResource(Res.drawable.inventory),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                StatCard(
                    title = "En Stock",
                    value = inStock.toString(),
                    icon = painterResource(Res.drawable.CheckCircle),
                    color = Color(0xFF22c55e),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                StatCard(
                    title = "Stock Faible",
                    value = lowStock.toString(),
                    icon = painterResource(Res.drawable.Warning),
                    color = Color(0xFFf59e0b),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                StatCard(
                    title = "Ruptures",
                    value = outOfStock.toString(),
                    icon = painterResource(Res.drawable.Error),
                    color = Color(0xFFef4444),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            // Valeur totale
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.Euro),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Valeur totale du stock",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "${totalValue} €",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


/**
 * Composable pour la section de recherche et de filtres.
 */
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
            .padding(horizontal = 8.dp, vertical = 6.dp) // Further reduced padding
            .shadow(
                elevation = 2.dp, // Further reduced elevation
                shape = RoundedCornerShape(8.dp) // Further reduced corner radius
            ),
        shape = RoundedCornerShape(8.dp), // Further reduced corner radius
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp), // Further reduced padding
            verticalArrangement = Arrangement.spacedBy(8.dp) // Further reduced spacing
        ) {
            // Barre de recherche
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Rechercher un produit...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp), // Further reduced corner radius
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // Filtres et tri
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp) // Further reduced spacing
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
                        shape = RoundedCornerShape(6.dp) // Further reduced corner radius
                    )
                    ExposedDropdownMenu(
                        expanded = filterExpanded,
                        onDismissRequest = { filterExpanded = false }
                    ) {
                        listOf("Tous", "En stock", "Stock faible", "Rupture", "Surstock").forEach { filter ->
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
                        shape = RoundedCornerShape(6.dp) // Further reduced corner radius
                    )
                    ExposedDropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false }
                    ) {
                        listOf("Nom", "Stock", "Prix", "Statut").forEach { sort ->
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

/**
 * Composable pour la liste des produits en stock.
 * Modifié pour inclure un `onItemClick` pour la sélection.
 */
@Composable
private fun StockItemsList(
    items: List<StockItem>,
    modifier: Modifier = Modifier,
    onItemClick: (StockItem) -> Unit
) {
    LazyColumn(
        // LazyColumn gère son propre défilement, donc pas besoin de verticalScroll ici.
        // Les paddings sont appliqués directement ici.
        modifier = modifier.padding(horizontal = 16.dp), // Reduced horizontal padding
        verticalArrangement = Arrangement.spacedBy(10.dp), // Reduced spacing
        contentPadding = PaddingValues(vertical = 12.dp) // Reduced vertical padding
    ) {
        items(items) { item ->
            StockItemCard(item = item, onClick = { onItemClick(item) })
        }
    }
}

/**
 * Composable pour une carte d'élément de stock individuelle.
 * Modifié pour être cliquable et afficher une progression animée.
 */
@Composable
private fun StockItemCard(item: StockItem, onClick: () -> Unit) {
    // Progress animée
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(item.currentStock, item.maxStock) {
        progress = (item.currentStock.toFloat() / item.maxStock.toFloat()).coerceIn(0f, 1f)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "Card Stock Progress Animation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = item.status.color.copy(alpha = 0.1f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // En-tête
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = item.status.color.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            painter = painterResource(item.status.icon),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = item.status.color
                        )
                        Text(
                            text = item.status.label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = item.status.color
                        )
                    }
                }
            }

            // Infos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoItem(
                    label = "Stock actuel",
                    value = "${item.currentStock} unités",
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Prix unitaire",
                    value = "${item.price} €",
                    modifier = Modifier.weight(1f)
                )
            }

            // Progression animée
            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Niveau de stock",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.currentStock}/${item.maxStock}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = when {
                        item.currentStock <= item.minStock -> Color(0xFFef4444)
                        item.currentStock >= item.maxStock * 0.8 -> Color(0xFF3b82f6)
                        else -> Color(0xFF22c55e)
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            }
        }
    }
}


/**
 * Composable pour afficher une paire label-valeur.
 */
@Composable
private fun InfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy( // Reduced font size
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
