package org.babetech.borastock.application.cli.command

/**
 * Interface de base pour toutes les commandes CLI
 */
interface Command {
    suspend fun execute(args: List<String>): Boolean
    fun getHelp(): String
}

/**
 * Commande d'aide
 */
class HelpCommand : Command {
    override suspend fun execute(args: List<String>): Boolean {
        println("📚 Commandes disponibles:")
        println()
        println("  help                    - Affiche cette aide")
        println("  dashboard               - Affiche le tableau de bord")
        println("  product list            - Liste tous les produits")
        println("  product create          - Crée un nouveau produit")
        println("  product show <id>       - Affiche les détails d'un produit")
        println("  supplier list           - Liste tous les fournisseurs")
        println("  supplier create         - Crée un nouveau fournisseur")
        println("  supplier show <id>      - Affiche les détails d'un fournisseur")
        println("  stock summary           - Affiche le résumé des stocks")
        println("  stock low               - Affiche les produits en stock faible")
        println("  movement list           - Liste les mouvements récents")
        println("  movement entry          - Enregistre une entrée de stock")
        println("  movement exit           - Enregistre une sortie de stock")
        println("  exit                    - Quitte l'application")
        println()
        return true
    }
    
    override fun getHelp(): String = "Affiche la liste des commandes disponibles"
}

/**
 * Commande de sortie
 */
class ExitCommand : Command {
    override suspend fun execute(args: List<String>): Boolean {
        println("🛑 Fermeture de l'application...")
        return false
    }
    
    override fun getHelp(): String = "Quitte l'application"
}