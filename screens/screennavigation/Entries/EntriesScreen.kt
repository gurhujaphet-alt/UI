package org.babetech.borastock.ui.screens.screennavigation.Entries


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState // Import ajouté pour l'état de défilement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll // Import ajouté pour le défilement vertical
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog // Import for Dialog
import borastock.composeapp.generated.resources.CheckCircle
import borastock.composeapp.generated.resources.Input
import borastock.composeapp.generated.resources.Receipt
import borastock.composeapp.generated.resources.Res
import borastock.composeapp.generated.resources.Schedule
import borastock.composeapp.generated.resources.TrendingUp
import borastock.composeapp.generated.resources.ic_close
import borastock.composeapp.generated.resources.inventory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.babetech.borastock.ui.screens.screennavigation.exits.StatCard
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


data class StockEntry(
    val id: String,
    val productName: String,
    val category: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalValue: Double,
    val supplier: String,
    val entryDate: LocalDateTime,
    val batchNumber: String?,
    val expiryDate: LocalDateTime?,
    val status: EntryStatus,
    val notes: String?
)

enum class EntryStatus(val label: String, val color: Color, val iconRes: DrawableResource) {
    PENDING("En attente", Color(0xFFf59e0b), Res.drawable.Schedule),
    VALIDATED("Validée", Color(0xFF22c55e), Res.drawable.CheckCircle),
    RECEIVED("Reçue", Color(0xFF3b82f6), Res.drawable.inventory),
    CANCELLED("Annulée", Color(0xFFef4444), Res.drawable.ic_close)
}


/**
 * Composable principal qui utilise `SupportingPaneScaffold` pour une mise en page adaptative.
 * Il affiche la liste des entrées à gauche (mainPane) et les détails de l'entrée sélectionnée à droite (supportingPane).
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalTime::class)
@Composable
fun EntriesScreen() {
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    var selectedEntry by remember { mutableStateOf<StockEntry?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Toutes") }
    var sortBy by remember { mutableStateOf("Date") }

    // État pour contrôler la visibilité du dialogue d'ajout
    var showAddEntryDialog by remember { mutableStateOf(false) }
    // Nouveaux états pour contrôler la visibilité du dialogue de modification
    var showEditEntryDialog by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<StockEntry?>(null) }


    val instantNow = Clock.System.now()
    fun nowMinusHours(hours: Int): LocalDateTime {
        return Clock.System.now()
            .minus(hours, DateTimeUnit.HOUR)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }


    // Données d'exemple (mutable pour pouvoir ajouter de nouvelles entrées)
    val stockEntries = remember {
        mutableStateListOf( // Utilisation de mutableStateListOf pour permettre les modifications
            StockEntry(
                id = "E001",
                productName = "iPhone 15 Pro Max",
                category = "Électronique",
                quantity = 50,
                unitPrice = 1199.99,
                totalValue = 59999.50,
                supplier = "Apple Inc.",
                entryDate = nowMinusHours(2),
                batchNumber = "APL2024001",
                expiryDate = null,
                status = EntryStatus.RECEIVED,
                notes = "Livraison conforme, emballage parfait"
            ),
            StockEntry(
                id = "E002",
                productName = "Samsung Galaxy S24 Ultra",
                category = "Électronique",
                quantity = 30,
                unitPrice = 1299.99,
                totalValue = 38999.70,
                supplier = "Samsung Electronics",
                entryDate = nowMinusHours(4),
                batchNumber = "SAM2024002",
                expiryDate = null,
                status = EntryStatus.VALIDATED,
                notes = "En attente de réception"
            ),
            StockEntry(
                id = "E003",
                productName = "MacBook Air M3",
                category = "Informatique",
                quantity = 15,
                unitPrice = 1299.99,
                totalValue = 19499.85,
                supplier = "Apple Inc.",
                entryDate = nowMinusHours(6),
                batchNumber = "APL2024003",
                expiryDate = null,
                status = EntryStatus.PENDING,
                notes = "Commande passée, livraison prévue demain"
            ),
            StockEntry(
                id = "E004",
                productName = "AirPods Pro 2",
                category = "Audio",
                quantity = 100,
                unitPrice = 279.99,
                totalValue = 27999.00,
                supplier = "Apple Inc.",
                entryDate = nowMinusHours(28),
                batchNumber = "APL2024004",
                expiryDate = null,
                status = EntryStatus.RECEIVED,
                notes = "Stock complet reçu"
            ),
            StockEntry(
                id = "E005",
                productName = "Dell XPS 13",
                category = "Informatique",
                quantity = 20,
                unitPrice = 999.99,
                totalValue = 19999.80,
                supplier = "Dell Technologies",
                entryDate = nowMinusHours(12),
                batchNumber = "DELL2024001",
                expiryDate = null,
                status = EntryStatus.CANCELLED,
                notes = "Annulée - problème de qualité"
            ),
            StockEntry(
                id = "E006",
                productName = "Sony WH-1000XM5",
                category = "Audio",
                quantity = 40,
                unitPrice = 399.99,
                totalValue = 15999.60,
                supplier = "Sony Corporation",
                entryDate = nowMinusHours(1),
                batchNumber = "SONY2024001",
                expiryDate = null,
                status = EntryStatus.RECEIVED,
                notes = "Excellent état, emballage premium"
            )
        )
    }

    val filteredEntries = stockEntries.filter { entry ->
        val matchesSearch = entry.productName.contains(searchQuery, ignoreCase = true) ||
                entry.category.contains(searchQuery, ignoreCase = true) ||
                entry.supplier.contains(searchQuery, ignoreCase = true) ||
                entry.batchNumber?.contains(searchQuery, ignoreCase = true) == true

        val matchesFilter = when (selectedFilter) {
            "Toutes" -> true
            "En attente" -> entry.status == EntryStatus.PENDING
            "Validées" -> entry.status == EntryStatus.VALIDATED
            "Reçues" -> entry.status == EntryStatus.RECEIVED
            "Annulées" -> entry.status == EntryStatus.CANCELLED
            else -> true
        }

        matchesSearch && matchesFilter
    }.let { entries ->
        when (sortBy) {
            "Date" -> entries.sortedByDescending { it.entryDate }
            "Produit" -> entries.sortedBy { it.productName }
            "Quantité" -> entries.sortedByDescending { it.quantity }
            "Valeur" -> entries.sortedByDescending { it.totalValue }
            "Statut" -> entries.sortedBy { it.status.label }
            else -> entries
        }
    }

    // Effet pour sélectionner la première entrée si la liste filtrée n'est pas vide
    // et qu'aucune entrée n'est sélectionnée. Utile pour les grands écrans.
    LaunchedEffect(filteredEntries) {
        if (selectedEntry == null && filteredEntries.isNotEmpty()) {
            selectedEntry = filteredEntries.first()
        }
    }

    SupportingPaneScaffold(
        value = navigator.scaffoldValue,
        directive = navigator.scaffoldDirective,
        mainPane = {
            AnimatedPane {
                EntriesMainPane(
                    entries = filteredEntries,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it },
                    sortBy = sortBy,
                    onSortChange = { sortBy = it },
                    onEntrySelected = { entry ->
                        selectedEntry = entry
                        scope.launch {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                        }
                    },
                    onAddEntryClick = {
                        showAddEntryDialog = true // Ouvre le dialogue d'ajout
                    }
                )
            }
        },
        supportingPane = {
            AnimatedPane {
                selectedEntry?.let { entry ->
                    EntryDetailPane(
                        entry = entry,
                        onBack = {
                            scope.launch { navigator.navigateBack() }
                        },
                        showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Main] == PaneAdaptedValue.Hidden,
                        onEditClick = { entryToModify -> // Nouveau lambda pour le bouton Modifier
                            entryToEdit = entryToModify
                            showEditEntryDialog = true
                        }
                    )
                } ?: run {
                    // Message de placeholder si aucune entrée n'est sélectionnée
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sélectionnez une entrée pour voir les détails",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )

    // Dialogue d'ajout de nouvelle entrée
    if (showAddEntryDialog) {
        AddEntryFormDialog(
            onDismiss = { showAddEntryDialog = false },
            onAddEntry = { newEntry ->
                val newId = (stockEntries.maxOfOrNull { it.id.substring(1).toInt() } ?: 0) + 1
                val entryToAdd = newEntry.copy(
                    id = "E${ newId}",
                    entryDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    totalValue = newEntry.quantity * newEntry.unitPrice,
                    status = EntryStatus.PENDING // Nouvelle entrée est par défaut en attente
                )
                stockEntries.add(0, entryToAdd) // Ajoute la nouvelle entrée au début de la liste
                selectedEntry = entryToAdd // Sélectionne la nouvelle entrée
                showAddEntryDialog = false // Ferme le dialogue
                scope.launch {
                    navigator.navigateTo(SupportingPaneScaffoldRole.Supporting) // Affiche les détails de la nouvelle entrée
                }
            }
        )
    }

    // Nouveau dialogue pour la modification d'une entrée
    if (showEditEntryDialog) {
        entryToEdit?.let { entry ->
            EditEntryFormDialog(
                initialEntry = entry,
                onDismiss = { showEditEntryDialog = false },
                onEditEntry = { updatedEntry ->
                    val index = stockEntries.indexOfFirst { it.id == updatedEntry.id }
                    if (index != -1) {
                        // Met à jour l'entrée existante avec les nouvelles valeurs, en recalculant totalValue
                        stockEntries[index] = updatedEntry.copy(totalValue = updatedEntry.quantity * updatedEntry.unitPrice)
                        selectedEntry = updatedEntry // Met à jour l'entrée sélectionnée si c'était celle qui était modifiée
                    }
                    showEditEntryDialog = false // Ferme le dialogue
                }
            )
        }
    }
}

/**
 * Composable principal pour le volet de gauche (liste des entrées).
 * Contient l'en-tête, la barre de recherche/filtres et la liste des entrées.
 */
@Composable
private fun EntriesMainPane(
    entries: List<StockEntry>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    sortBy: String,
    onSortChange: (String) -> Unit, // Corrected: This parameter is now directly passed
    onEntrySelected: (StockEntry) -> Unit,
    onAddEntryClick: () -> Unit // Nouveau paramètre pour l'action d'ajout
) {
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
        // Header avec statistiques ajouté comme un item dans LazyColumn
        item {
            EntriesHeader(entries = entries)
        }

        // Barre de recherche et filtres ajoutée comme un item dans LazyColumn
        item {
            SearchAndFiltersSection(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                selectedFilter = selectedFilter,
                onFilterChange = onFilterChange,
                sortBy = sortBy,
                onSortChange = onSortChange // Corrected: Pass the lambda directly
            )
        }

        // Liste des entrées
        items(entries) { entry ->
            EntryCard(
                entry = entry,
                onClick = { onEntrySelected(entry) }
            )
        }

        // Bouton d'ajout flottant
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = onAddEntryClick, // Utilise le nouveau paramètre d'action
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp)
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Ajouter une entrée",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Composable pour l'en-tête de l'écran des entrées, affichant les statistiques globales.
 */
@Composable
fun EntriesHeader(entries: List<StockEntry>) {
    val totalEntries = entries.size
    val pendingEntries = entries.count { it.status == EntryStatus.PENDING }
    val receivedEntries = entries.count { it.status == EntryStatus.RECEIVED }
    val totalValue = entries.filter { it.status != EntryStatus.CANCELLED }.sumOf { it.totalValue }

    StockHeader(
        title = "Entrées de Stock",
        subtitle = "Gestion des réceptions et commandes",
        icon = painterResource(Res.drawable.Input),
        iconColor = MaterialTheme.colorScheme.primary,
        stats = listOf(
            StockStat(
                "Total Entrées",
                totalEntries.toString(),
                Res.drawable.Receipt,
                MaterialTheme.colorScheme.primary
            ),
            StockStat(
                "En Attente",
                pendingEntries.toString(),
                Res.drawable.Schedule,
                Color(0xFFf59e0b)
            ),
            StockStat(
                "Reçues",
                receivedEntries.toString(),
                Res.drawable.CheckCircle,
                Color(0xFF22c55e)
            )
        ),
        summaries = listOf(
            StockSummary(
                "Valeur totale des entrées",
                "${totalValue} €",
                Res.drawable.TrendingUp,
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                MaterialTheme.colorScheme.primary
            )
        )
    )
}


/**
 * Composable pour la section de recherche et de filtres des entrées.
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
            // Barre de recherche
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Rechercher une entrée...") },
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
                        listOf("Toutes", "En attente", "Validées", "Reçues", "Annulées").forEach { filter ->
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
                        listOf("Date", "Produit", "Quantité", "Valeur", "Statut").forEach { sort ->
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
 * Composable pour la liste des entrées de stock.
 * Modifié pour inclure un `onEntrySelected` pour la sélection.
 */
@Composable
private fun EntriesList(
    entries: List<StockEntry>,
    onEntrySelected: (StockEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp), // Réduction du padding horizontal
        verticalArrangement = Arrangement.spacedBy(10.dp), // Réduction de l'espacement
        contentPadding = PaddingValues(vertical = 12.dp) // Réduction du padding vertical
    ) {
        items(entries) { entry ->
            EntryCard(
                entry = entry,
                onClick = { onEntrySelected(entry) }
            )
        }
    }
}

/**
 * Composable pour une carte d'entrée individuelle.
 * Modifié pour être cliquable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryCard(
    entry: StockEntry,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp, // Réduction de l'élévation
                shape = RoundedCornerShape(12.dp), // Réduction du rayon des coins
                spotColor = entry.status.color.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(12.dp), // Réduction du rayon des coins
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp), // Réduction du padding
            verticalArrangement = Arrangement.spacedBy(10.dp) // Réduction de l'espacement
        ) {
            // En-tête avec nom et statut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.productName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = entry.category,
                        style = MaterialTheme.typography.bodySmall, // Réduction de la taille de la police
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Card(
                    shape = RoundedCornerShape(16.dp), // Réduction du rayon des coins
                    colors = CardDefaults.cardColors(
                        containerColor = entry.status.color.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), // Réduction du padding
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp) // Réduction de l'espacement
                    ) {
                        Icon(
                            painter = painterResource(entry.status.iconRes),
                            contentDescription = null,
                            tint = entry.status.color,
                            modifier = Modifier.size(12.dp) // Réduction de la taille
                        )
                        Text(
                            text = entry.status.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = entry.status.color
                        )
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
                    value = "${entry.quantity} unités",
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Prix unitaire",
                    value = "${entry.unitPrice} €",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Réduction de l'espacement
            ) {
                InfoItem(
                    label = "Valeur totale",
                    value = "${entry.totalValue} €",
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Date d'entrée",
                    value = entry.entryDate.toString(), // Formatage de la date
                    modifier = Modifier.weight(1f)
                )
            }

            // Fournisseur et lot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Réduction de l'espacement
            ) {
                InfoItem(
                    label = "Fournisseur",
                    value = entry.supplier,
                    modifier = Modifier.weight(1f)
                )
                entry.batchNumber?.let { batch ->
                    InfoItem(
                        label = "N° de lot",
                        value = batch,
                        modifier = Modifier.weight(1f)
                    )
                }
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
            style = MaterialTheme.typography.bodySmall.copy( // Réduction de la taille de la police
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Composable pour le volet de détails d'une entrée de stock.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryDetailPane(
    entry: StockEntry,
    onBack: () -> Unit,
    showBackButton: Boolean,
    onEditClick: (StockEntry) -> Unit // Nouveau paramètre pour l'action de modification
) {
    val scrollState = rememberScrollState() // Ajout de l'état de défilement

    fun LocalDateTime.formatToString(): String {
        return "${dayOfMonth.toString().padStart(2, '0')}/" +
                "${monthNumber.toString().padStart(2, '0')}/" +
                "$year à ${hour.toString().padStart(2, '0')}:" +
                "${minute.toString().padStart(2, '0')}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(entry.productName) }, // Utilisation du nom du produit comme titre
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBack) {
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
                .padding(horizontal = 12.dp, vertical = 8.dp) // Réduction du padding
                .verticalScroll(scrollState), // Ajout du modificateur de défilement vertical
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp) // Réduction de l'espacement
        ) {
            // En-tête de la carte de détails
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp) // Réduction de l'espacement
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp) // Réduction de la taille
                                .clip(RoundedCornerShape(10.dp)) // Réduction du rayon des coins
                                .background(entry.status.color.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(entry.status.iconRes),
                                contentDescription = null,
                                tint = entry.status.color,
                                modifier = Modifier.size(20.dp) // Réduction de la taille
                            )
                        }

                        Column {
                            Text(
                                text = entry.productName,
                                style = MaterialTheme.typography.headlineSmall.copy( // Ajustement de la taille de la police
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Entrée ${entry.id}",
                                style = MaterialTheme.typography.bodyMedium, // Réduction de la taille de la police
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Détails complets
                    DetailSection(
                        title = "Informations produit",
                        items = listOf(
                            "Catégorie" to entry.category,
                            "Quantité" to "${entry.quantity} unités",
                            "Prix unitaire" to "${entry.unitPrice} €",
                            "Valeur totale" to "${entry.totalValue} €"
                        )
                    )

                    DetailSection(
                        title = "Informations fournisseur",
                        items = listOf(
                            "Fournisseur" to entry.supplier,
                            "N° de lot" to (entry.batchNumber ?: "Non spécifié"),
                            "Date d'entrée" to entry.entryDate.formatToString(),
                            "Statut" to entry.status.label
                        )
                    )

                    entry.notes?.let { notes ->
                        DetailSection(
                            title = "Notes",
                            items = listOf("Commentaires" to notes)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // Réduction de l'espacement

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Réduction de l'espacement
            ) {
                OutlinedButton(
                    onClick = { onEditClick(entry) }, // Appel de l'action de modification
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp), // Réduction du rayon des coins
                    contentPadding = PaddingValues(10.dp) // Réduction du padding
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp) // Réduction de la taille
                    )
                    Spacer(modifier = Modifier.width(6.dp)) // Réduction de l'espacement
                    Text("Modifier", style = MaterialTheme.typography.labelLarge) // Ajustement de la taille de la police
                }

                Button(
                    onClick = { /* TODO: Valider l'entrée */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp), // Réduction du rayon des coins
                    contentPadding = PaddingValues(10.dp), // Réduction du padding
                    enabled = entry.status == EntryStatus.PENDING
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp) // Réduction de la taille
                    )
                    Spacer(modifier = Modifier.width(6.dp)) // Réduction de l'espacement
                    Text("Valider", style = MaterialTheme.typography.labelLarge) // Ajustement de la taille de la police
                }
            }
        }
    }
}

/**
 * Composable pour une section de détails avec un titre et une liste d'éléments.
 */
@Composable
private fun DetailSection(
    title: String,
    items: List<Pair<String, String>>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp) // Réduction de l'espacement
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy( // Ajustement de la taille de la police
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        items.forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall, // Réduction de la taille de la police
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall.copy( // Réduction de la taille de la police
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Nouveau composable pour le formulaire d'ajout d'une nouvelle entrée.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun AddEntryFormDialog(
    onDismiss: () -> Unit,
    onAddEntry: (StockEntry) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("0") }
    var unitPrice by remember { mutableStateOf("0.0") }
    var supplier by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") } // État pour le message d'erreur

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()), // Rendre le contenu du dialogue défilable
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ajouter une nouvelle entrée",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = productName,
                    onValueChange = {
                        productName = it
                        errorMessage = "" // Efface l'erreur lors de la saisie
                    },
                    label = { Text("Nom du produit") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = {
                        category = it
                        errorMessage = ""
                    },
                    label = { Text("Catégorie") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*"))) quantity = it
                        errorMessage = ""
                    },
                    label = { Text("Quantité") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = unitPrice,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*"))) unitPrice = it
                        errorMessage = ""
                    },
                    label = { Text("Prix unitaire (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = supplier,
                    onValueChange = {
                        supplier = it
                        errorMessage = ""
                    },
                    label = { Text("Fournisseur") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )

                // Message d'erreur
                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Annuler")
                    }
                    Button(
                        onClick = {
                            val parsedQuantity = quantity.toIntOrNull() ?: 0
                            val parsedUnitPrice = unitPrice.toDoubleOrNull() ?: 0.0
                            if (productName.isNotBlank() && category.isNotBlank() && supplier.isNotBlank() && parsedQuantity > 0 && parsedUnitPrice > 0) {
                                val newEntry = StockEntry(
                                    id = "", // Sera généré dans EntriesScreen
                                    productName = productName,
                                    category = category,
                                    quantity = parsedQuantity,
                                    unitPrice = parsedUnitPrice,
                                    totalValue = 0.0, // Sera calculé dans EntriesScreen
                                    supplier = supplier,
                                    entryDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), // Sera mis à jour dans EntriesScreen
                                    batchNumber = null, // Pour la simplicité
                                    expiryDate = null, // Pour la simplicité
                                    status = EntryStatus.PENDING, // Sera mis à jour dans EntriesScreen
                                    notes = notes.ifBlank { null }
                                )
                                onAddEntry(newEntry)
                            } else {
                                errorMessage = "Veuillez remplir tous les champs obligatoires (Nom, Catégorie, Quantité, Prix Unitaire, Fournisseur) et s'assurer que Quantité et Prix Unitaire sont > 0."
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Ajouter")
                    }
                }
            }
        }
    }
}

/**
 * Nouveau composable pour le formulaire de modification d'une entrée existante.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEntryFormDialog(
    initialEntry: StockEntry, // L'entrée à modifier
    onDismiss: () -> Unit,
    onEditEntry: (StockEntry) -> Unit
) {
    var productName by remember { mutableStateOf(initialEntry.productName) }
    var category by remember { mutableStateOf(initialEntry.category) }
    var quantity by remember { mutableStateOf(initialEntry.quantity.toString()) }
    var unitPrice by remember { mutableStateOf(initialEntry.unitPrice.toString()) }
    var supplier by remember { mutableStateOf(initialEntry.supplier) }
    var batchNumber by remember { mutableStateOf(initialEntry.batchNumber ?: "") }
    var notes by remember { mutableStateOf(initialEntry.notes ?: "") }
    var errorMessage by remember { mutableStateOf("") } // État pour le message d'erreur

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()), // Rendre le contenu du dialogue défilable
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Modifier l'entrée",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = productName,
                    onValueChange = {
                        productName = it
                        errorMessage = ""
                    },
                    label = { Text("Nom du produit") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = {
                        category = it
                        errorMessage = ""
                    },
                    label = { Text("Catégorie") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*"))) quantity = it
                        errorMessage = ""
                    },
                    label = { Text("Quantité") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = unitPrice,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*"))) unitPrice = it
                        errorMessage = ""
                    },
                    label = { Text("Prix unitaire (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = supplier,
                    onValueChange = {
                        supplier = it
                        errorMessage = ""
                    },
                    label = { Text("Fournisseur") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = batchNumber,
                    onValueChange = { batchNumber = it },
                    label = { Text("N° de lot (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )

                // Message d'erreur
                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Annuler")
                    }
                    Button(
                        onClick = {
                            val parsedQuantity = quantity.toIntOrNull() ?: 0
                            val parsedUnitPrice = unitPrice.toDoubleOrNull() ?: 0.0
                            if (productName.isNotBlank() && category.isNotBlank() && supplier.isNotBlank() && parsedQuantity > 0 && parsedUnitPrice > 0) {
                                val updatedEntry = initialEntry.copy(
                                    productName = productName,
                                    category = category,
                                    quantity = parsedQuantity,
                                    unitPrice = parsedUnitPrice,
                                    supplier = supplier,
                                    batchNumber = batchNumber.ifBlank { null },
                                    notes = notes.ifBlank { null }
                                )
                                onEditEntry(updatedEntry)
                            } else {
                                errorMessage = "Veuillez remplir tous les champs obligatoires (Nom, Catégorie, Quantité, Prix Unitaire, Fournisseur) et s'assurer que Quantité et Prix Unitaire sont > 0."
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Modifier")
                    }
                }
            }
        }
    }
}




@Composable
fun StockHeader(
    title: String,
    subtitle: String,
    icon: Painter,
    iconColor: Color,
    stats: List<StockStat>,
    summaries: List<StockSummary>,
    modifier: Modifier = Modifier,
    animateStats: Boolean = true
) {
    Card(
        modifier = modifier
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
                            .background(iconColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Statistiques
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stats.forEachIndexed { index, stat ->
                    var visible by remember { mutableStateOf(!animateStats) }

//                    LaunchedEffect(Unit) {
//                        if (animateStats) {
//                            delay(index * 150L)
//                            visible = true
//                        }
//                    }

//                    AnimatedVisibility(
//                        visible = visible,
//                        enter = scaleIn(
//                            animationSpec = spring(
//                                dampingRatio = Spring.DampingRatioMediumBouncy,
//                                stiffness = Spring.StiffnessMedium
//                            )
//                        ) + fadeIn()
//                    ) {
                        StatCard(
                            title = stat.title,
                            value = stat.value,
                            icon = painterResource(stat.iconRes),
                            color = stat.color,
                            modifier = Modifier.weight(1f) // seulement weight, pas de fillMaxHeight
                        )
                //    }
                }
            }


            // Résumés
            summaries.forEach { summary ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = summary.backgroundColor
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
                                painter = painterResource(summary.iconRes),
                                contentDescription = null,
                                tint = summary.iconTint,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = summary.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = summary.value,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = summary.valueColor
                        )
                    }
                }
            }
        }
    }
}


// 📦 Modèle de donnée pour les stats
data class StockStat(
    val title: String,
    val value: String,
    val iconRes: DrawableResource,
    val color: Color
)

// 📦 Modèle de donnée pour les résumés
data class StockSummary(
    val label: String,
    val value: String,
    val iconRes: DrawableResource,
    val iconTint: Color,
    val backgroundColor: Color,
    val valueColor: Color
)

