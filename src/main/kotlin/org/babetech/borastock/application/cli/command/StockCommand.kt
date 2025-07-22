package org.babetech.borastock.application.cli.command

import org.babetech.borastock.domain.model.StockStatus
import org.babetech.borastock.domain.service.ProductService
import org.babetech.borastock.domain.service.StockMovementService

/**
 * Commandes pour la gestion des stocks
 */
class StockCommand(
    private val productService: ProductService,
    private val stockMovementService: StockMovementService
) : Command {
    
    override suspend fun execute(args: List<String>): Boolean {
        if (args.isEmpty()) {
            println("❌ Sous-commande manquante. Utilisez: stock <summary|low|status>")
            return true
        }
        
        when (args[0].lowercase()) {
            "summary" -> showStockSummary()
            "low" -> showLowStockProducts()
            "status" -> showStockByStatus(args.drop(1))
            else -> println("❌ Sous-commande inconnue: ${args[0]}")
        }
        
        return true
    }
    
    private suspend fun showStockSummary() {
        val summary = productService.getStockSummary()
        
        println("📊 Résumé des stocks")
        println("=" * 30)
        println()
        println("Total produits: ${summary.totalProducts}")
        println("En stock: ${summary.inStock}")
        println("Stock faible: ${summary.lowStock}")
        println("Ruptures: ${summary.outOfStock}")
        println("Surstock: ${summary.overstocked}")
        println()
        println("Santé du stock: ${"%.1f".format(summary.healthyStockPercentage)}%")
        
        // Graphique simple en ASCII
        if (summary.totalProducts > 0) {
            println()
            println("Répartition:")
            val inStockBar = "█".repeat((summary.inStock * 20 / summary.totalProducts).toInt())
            val lowStockBar = "▓".repeat((summary.lowStock * 20 / summary.totalProducts).toInt())
            val outStockBar = "░".repeat((summary.outOfStock * 20 / summary.totalProducts).toInt())
            
            println("En stock:    [$inStockBar${" ".repeat(20 - inStockBar.length)}] ${summary.inStock}")
            println("Stock faible:[$lowStockBar${" ".repeat(20 - lowStockBar.length)}] ${summary.lowStock}")
            println("Ruptures:    [$outStockBar${" ".repeat(20 - outStockBar.length)}] ${summary.outOfStock}")
        }
    }
    
    private suspend fun showLowStockProducts() {
        val lowStockProducts = productService.getLowStockProducts()
        
        if (lowStockProducts.isEmpty()) {
            println("✅ Aucun produit en stock faible!")
            return
        }
        
        println("⚠️  Produits en stock faible (${lowStockProducts.size}):")
        println()
        println("%-30s %-10s %-10s %-10s".format("Nom", "Stock", "Min", "Max"))
        println("-".repeat(60))
        
        lowStockProducts.forEach { product ->
            println("%-30s %-10d %-10d %-10d".format(
                product.name.take(28),
                product.stock.currentQuantity,
                product.stock.minThreshold,
                product.stock.maxCapacity
            ))
        }
    }
    
    private suspend fun showStockByStatus(args: List<String>) {
        if (args.isEmpty()) {
            println("❌ Statut manquant. Utilisez: in_stock, low_stock, out_of_stock, overstocked")
            return
        }
        
        val status = when (args[0].lowercase()) {
            "in_stock", "in" -> StockStatus.IN_STOCK
            "low_stock", "low" -> StockStatus.LOW_STOCK
            "out_of_stock", "out" -> StockStatus.OUT_OF_STOCK
            "overstocked", "over" -> StockStatus.OVERSTOCKED
            else -> {
                println("❌ Statut invalide: ${args[0]}")
                return
            }
        }
        
        val products = productService.getProductsByStatus(status)
        
        if (products.isEmpty()) {
            println("📦 Aucun produit avec le statut: ${status.label}")
            return
        }
        
        println("📦 Produits avec le statut '${status.label}' (${products.size}):")
        println()
        println("%-30s %-10s %-15s".format("Nom", "Stock", "Prix"))
        println("-".repeat(55))
        
        products.forEach { product ->
            println("%-30s %-10d %-15s".format(
                product.name.take(28),
                product.stock.currentQuantity,
                product.price.toString()
            ))
        }
    }
    
    override fun getHelp(): String = "Gestion des stocks (summary, low, status)"
}

private operator fun String.times(n: Int): String = this.repeat(n)