package org.babetech.borastock.application.cli.command

import org.babetech.borastock.domain.model.*
import org.babetech.borastock.domain.service.StockMovementService

/**
 * Commandes pour la gestion des mouvements de stock
 */
class MovementCommand(
    private val stockMovementService: StockMovementService
) : Command {
    
    override suspend fun execute(args: List<String>): Boolean {
        if (args.isEmpty()) {
            println("❌ Sous-commande manquante. Utilisez: movement <list|entry|exit>")
            return true
        }
        
        when (args[0].lowercase()) {
            "list" -> listMovements()
            "entry" -> recordEntry()
            "exit" -> recordExit()
            "summary" -> showMovementSummary()
            else -> println("❌ Sous-commande inconnue: ${args[0]}")
        }
        
        return true
    }
    
    private suspend fun listMovements() {
        val movements = stockMovementService.getRecentMovements(20)
        
        if (movements.isEmpty()) {
            println("📈 Aucun mouvement trouvé.")
            return
        }
        
        println("📈 Mouvements récents (${movements.size}):")
        println()
        println("%-15s %-8s %-10s %-30s".format("Type", "Qté", "Produit", "Raison"))
        println("-".repeat(70))
        
        movements.forEach { movement ->
            val type = if (movement.isEntry()) "📥 Entrée" else "📤 Sortie"
            
            println("%-15s %-8d %-10s %-30s".format(
                type,
                movement.quantity,
                movement.productId.value.take(8) + "...",
                movement.reason.take(28)
            ))
        }
    }
    
    private suspend fun recordEntry() {
        println("📥 Enregistrement d'une entrée de stock")
        println()
        
        print("ID du produit: ")
        val productIdInput = readlnOrNull()?.trim() ?: return
        val productId = ProductId(productIdInput)
        
        print("Quantité: ")
        val quantityInput = readlnOrNull()?.trim() ?: return
        val quantity = try {
            quantityInput.toInt()
        } catch (e: NumberFormatException) {
            println("❌ Quantité invalide")
            return
        }
        
        print("Raison: ")
        val reason = readlnOrNull()?.trim() ?: return
        
        print("Référence (optionnel): ")
        val reference = readlnOrNull()?.trim()?.takeIf { it.isNotEmpty() }
        
        // Pour la démo, on utilise un utilisateur par défaut
        val performedBy = UserId.generate()
        
        val result = stockMovementService.recordEntry(
            productId = productId,
            quantity = quantity,
            reason = reason,
            performedBy = performedBy,
            reference = reference
        )
        
        result.fold(
            onSuccess = { movement ->
                println("✅ Entrée enregistrée avec succès!")
                println("   ID: ${movement.id.value}")
                println("   Quantité: ${movement.quantity}")
                println("   Raison: ${movement.reason}")
                println("   Horodatage: ${movement.timestamp}")
            },
            onFailure = { error ->
                println("❌ Erreur lors de l'enregistrement: ${error.message}")
            }
        )
    }
    
    private suspend fun recordExit() {
        println("📤 Enregistrement d'une sortie de stock")
        println()
        
        print("ID du produit: ")
        val productIdInput = readlnOrNull()?.trim() ?: return
        val productId = ProductId(productIdInput)
        
        print("Quantité: ")
        val quantityInput = readlnOrNull()?.trim() ?: return
        val quantity = try {
            quantityInput.toInt()
        } catch (e: NumberFormatException) {
            println("❌ Quantité invalide")
            return
        }
        
        print("Raison: ")
        val reason = readlnOrNull()?.trim() ?: return
        
        print("Référence (optionnel): ")
        val reference = readlnOrNull()?.trim()?.takeIf { it.isNotEmpty() }
        
        // Pour la démo, on utilise un utilisateur par défaut
        val performedBy = UserId.generate()
        
        val result = stockMovementService.recordExit(
            productId = productId,
            quantity = quantity,
            reason = reason,
            performedBy = performedBy,
            reference = reference
        )
        
        result.fold(
            onSuccess = { movement ->
                println("✅ Sortie enregistrée avec succès!")
                println("   ID: ${movement.id.value}")
                println("   Quantité: ${movement.quantity}")
                println("   Raison: ${movement.reason}")
                println("   Horodatage: ${movement.timestamp}")
            },
            onFailure = { error ->
                println("❌ Erreur lors de l'enregistrement: ${error.message}")
            }
        )
    }
    
    private suspend fun showMovementSummary() {
        val summary = stockMovementService.getMovementSummary()
        
        println("📊 Résumé des mouvements")
        println("=" * 25)
        println()
        println("Total mouvements: ${summary.totalMovements}")
        println("Entrées: ${summary.entries}")
        println("Sorties: ${summary.exits}")
        println("Mouvement net: ${summary.netMovement}")
        
        if (summary.totalMovements > 0) {
            val entryPercentage = (summary.entries.toDouble() / summary.totalMovements) * 100
            val exitPercentage = (summary.exits.toDouble() / summary.totalMovements) * 100
            
            println()
            println("Répartition:")
            println("Entrées: ${"%.1f".format(entryPercentage)}%")
            println("Sorties: ${"%.1f".format(exitPercentage)}%")
        }
    }
    
    override fun getHelp(): String = "Gestion des mouvements de stock (list, entry, exit, summary)"
}

private operator fun String.times(n: Int): String = this.repeat(n)