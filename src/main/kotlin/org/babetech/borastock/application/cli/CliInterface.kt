package org.babetech.borastock.application.cli

import mu.KotlinLogging
import org.babetech.borastock.infrastructure.di.DependencyContainer
import org.babetech.borastock.application.cli.command.*

private val logger = KotlinLogging.logger {}

/**
 * Interface en ligne de commande pour BoraStock
 */
class CliInterface(
    private val container: DependencyContainer
) {
    private val commands = mapOf(
        "help" to HelpCommand(),
        "product" to ProductCommand(container.productService),
        "supplier" to SupplierCommand(container.supplierService),
        "stock" to StockCommand(container.productService, container.stockMovementService),
        "movement" to MovementCommand(container.stockMovementService),
        "dashboard" to DashboardCommand(container.productService, container.supplierService, container.stockMovementService),
        "exit" to ExitCommand()
    )
    
    suspend fun start() {
        println("🎯 Bienvenue dans BoraStock CLI!")
        println("Tapez 'help' pour voir les commandes disponibles.")
        println("Tapez 'exit' pour quitter.")
        println()
        
        while (true) {
            print("borastock> ")
            val input = readlnOrNull()?.trim() ?: continue
            
            if (input.isEmpty()) continue
            
            val parts = input.split(" ")
            val commandName = parts[0].lowercase()
            val args = parts.drop(1)
            
            try {
                val command = commands[commandName]
                if (command != null) {
                    val result = command.execute(args)
                    if (!result) break // Exit command returns false
                } else {
                    println("❌ Commande inconnue: $commandName")
                    println("Tapez 'help' pour voir les commandes disponibles.")
                }
            } catch (e: Exception) {
                logger.error(e) { "Erreur lors de l'exécution de la commande: $commandName" }
                println("❌ Erreur: ${e.message}")
            }
            
            println()
        }
        
        println("👋 Au revoir!")
    }
}