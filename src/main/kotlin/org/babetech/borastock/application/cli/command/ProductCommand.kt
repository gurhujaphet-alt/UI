package org.babetech.borastock.application.cli.command

import org.babetech.borastock.domain.model.*
import org.babetech.borastock.domain.service.ProductService

/**
 * Commandes pour la gestion des produits
 */
class ProductCommand(
    private val productService: ProductService
) : Command {
    
    override suspend fun execute(args: List<String>): Boolean {
        if (args.isEmpty()) {
            println("❌ Sous-commande manquante. Utilisez: product <list|create|show>")
            return true
        }
        
        when (args[0].lowercase()) {
            "list" -> listProducts()
            "create" -> createProduct()
            "show" -> showProduct(args.drop(1))
            "search" -> searchProducts(args.drop(1))
            else -> println("❌ Sous-commande inconnue: ${args[0]}")
        }
        
        return true
    }
    
    private suspend fun listProducts() {
        val products = productService.getAllProducts()
        
        if (products.isEmpty()) {
            println("📦 Aucun produit trouvé.")
            return
        }
        
        println("📦 Liste des produits (${products.size}):")
        println()
        println("%-15s %-30s %-15s %-10s %-15s".format("ID", "Nom", "Catégorie", "Stock", "Statut"))
        println("-".repeat(85))
        
        products.forEach { product ->
            val status = when {
                product.isOutOfStock() -> "❌ Rupture"
                product.isLowStock() -> "⚠️ Faible"
                product.isOverstocked() -> "📈 Surstock"
                else -> "✅ Normal"
            }
            
            println("%-15s %-30s %-15s %-10d %s".format(
                product.id.value.take(12) + "...",
                product.name.take(28),
                product.category.name.take(13),
                product.stock.currentQuantity,
                status
            ))
        }
    }
    
    private suspend fun createProduct() {
        println("🆕 Création d'un nouveau produit")
        println()
        
        print("Nom du produit: ")
        val name = readlnOrNull()?.trim() ?: return
        
        print("Description (optionnel): ")
        val description = readlnOrNull()?.trim()?.takeIf { it.isNotEmpty() }
        
        print("Prix (EUR): ")
        val priceInput = readlnOrNull()?.trim() ?: return
        val price = try {
            Money(priceInput.toDouble())
        } catch (e: NumberFormatException) {
            println("❌ Prix invalide")
            return
        }
        
        print("Quantité initiale: ")
        val quantityInput = readlnOrNull()?.trim() ?: return
        val quantity = try {
            quantityInput.toInt()
        } catch (e: NumberFormatException) {
            println("❌ Quantité invalide")
            return
        }
        
        print("Seuil minimum: ")
        val minInput = readlnOrNull()?.trim() ?: return
        val minThreshold = try {
            minInput.toInt()
        } catch (e: NumberFormatException) {
            println("❌ Seuil minimum invalide")
            return
        }
        
        print("Capacité maximale: ")
        val maxInput = readlnOrNull()?.trim() ?: return
        val maxCapacity = try {
            maxInput.toInt()
        } catch (e: NumberFormatException) {
            println("❌ Capacité maximale invalide")
            return
        }
        
        // Pour la démo, on crée une catégorie et un fournisseur par défaut
        val category = Category(CategoryId.generate(), "Général")
        val supplierId = SupplierId.generate()
        
        val stock = try {
            Stock(quantity, minThreshold, maxCapacity)
        } catch (e: IllegalArgumentException) {
            println("❌ ${e.message}")
            return
        }
        
        val result = productService.createProduct(
            name = name,
            description = description,
            categoryId = category.id,
            price = price,
            stock = stock,
            supplierId = supplierId
        )
        
        result.fold(
            onSuccess = { product ->
                println("✅ Produit créé avec succès!")
                println("   ID: ${product.id.value}")
                println("   Nom: ${product.name}")
                println("   Prix: ${product.price}")
                println("   Stock: ${product.stock.currentQuantity}")
            },
            onFailure = { error ->
                println("❌ Erreur lors de la création: ${error.message}")
            }
        )
    }
    
    private suspend fun showProduct(args: List<String>) {
        if (args.isEmpty()) {
            println("❌ ID du produit manquant")
            return
        }
        
        val productId = ProductId(args[0])
        val product = productService.getProduct(productId)
        
        if (product == null) {
            println("❌ Produit non trouvé: ${args[0]}")
            return
        }
        
        println("📦 Détails du produit:")
        println()
        println("ID: ${product.id.value}")
        println("Nom: ${product.name}")
        println("Description: ${product.description ?: "Aucune"}")
        println("Catégorie: ${product.category.name}")
        println("Prix: ${product.price}")
        println("Stock actuel: ${product.stock.currentQuantity}")
        println("Seuil minimum: ${product.stock.minThreshold}")
        println("Capacité maximale: ${product.stock.maxCapacity}")
        println("Statut: ${product.stock.getStatus().label}")
        println("Créé le: ${product.createdAt}")
        println("Modifié le: ${product.updatedAt}")
    }
    
    private suspend fun searchProducts(args: List<String>) {
        if (args.isEmpty()) {
            println("❌ Terme de recherche manquant")
            return
        }
        
        val query = args.joinToString(" ")
        val products = productService.searchProducts(query)
        
        if (products.isEmpty()) {
            println("🔍 Aucun produit trouvé pour: '$query'")
            return
        }
        
        println("🔍 Résultats de recherche pour '$query' (${products.size}):")
        println()
        products.forEach { product ->
            println("- ${product.name} (${product.id.value}) - Stock: ${product.stock.currentQuantity}")
        }
    }
    
    override fun getHelp(): String = "Gestion des produits (list, create, show, search)"
}