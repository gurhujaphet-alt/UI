package org.babetech.borastock.application.cli.command

import org.babetech.borastock.domain.service.ProductService
import org.babetech.borastock.domain.service.SupplierService
import org.babetech.borastock.domain.service.StockMovementService

/**
 * Commande pour afficher le tableau de bord
 */
class DashboardCommand(
    private val productService: ProductService,
    private val supplierService: SupplierService,
    private val stockMovementService: StockMovementService
) : Command {
    
    override suspend fun execute(args: List<String>): Boolean {
        println("📊 Tableau de bord BoraStock")
        println("=" * 50)
        println()
        
        // Résumé des stocks
        val stockSummary = productService.getStockSummary()
        println("📦 STOCKS")
        println("   Total produits: ${stockSummary.totalProducts}")
        println("   En stock: ${stockSummary.inStock}")
        println("   Stock faible: ${stockSummary.lowStock}")
        println("   Ruptures: ${stockSummary.outOfStock}")
        println("   Surstock: ${stockSummary.overstocked}")
        println("   Santé du stock: ${"%.1f".format(stockSummary.healthyStockPercentage)}%")
        println()
        
        // Résumé des fournisseurs
        val supplierSummary = supplierService.getSupplierSummary()
        println("🏢 FOURNISSEURS")
        println("   Total: ${supplierSummary.totalSuppliers}")
        println("   Actifs: ${supplierSummary.activeSuppliers}")
        println("   Inactifs: ${supplierSummary.inactiveSuppliers}")
        println("   Taux d'activité: ${"%.1f".format(supplierSummary.activePercentage)}%")
        println()
        
        // Résumé des mouvements
        val movementSummary = stockMovementService.getMovementSummary()
        println("📈 MOUVEMENTS")
        println("   Total mouvements: ${movementSummary.totalMovements}")
        println("   Entrées: ${movementSummary.entries}")
        println("   Sorties: ${movementSummary.exits}")
        println("   Mouvement net: ${movementSummary.netMovement}")
        println()
        
        // Produits en alerte
        val lowStockProducts = productService.getLowStockProducts()
        if (lowStockProducts.isNotEmpty()) {
            println("⚠️  ALERTES STOCK FAIBLE")
            lowStockProducts.take(5).forEach { product ->
                println("   - ${product.name}: ${product.stock.currentQuantity} unités")
            }
            if (lowStockProducts.size > 5) {
                println("   ... et ${lowStockProducts.size - 5} autres")
            }
            println()
        }
        
        // Mouvements récents
        val recentMovements = stockMovementService.getRecentMovements(5)
        if (recentMovements.isNotEmpty()) {
            println("🕒 MOUVEMENTS RÉCENTS")
            recentMovements.forEach { movement ->
                val type = if (movement.isEntry()) "📥 Entrée" else "📤 Sortie"
                println("   $type: ${movement.quantity} unités - ${movement.reason}")
            }
            println()
        }
        
        return true
    }
    
    override fun getHelp(): String = "Affiche le tableau de bord avec les statistiques principales"
}

private operator fun String.times(n: Int): String = this.repeat(n)