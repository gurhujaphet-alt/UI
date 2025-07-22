package org.babetech.borastock.application

import mu.KotlinLogging
import org.babetech.borastock.infrastructure.di.DependencyContainer
import org.babetech.borastock.application.cli.CliInterface

private val logger = KotlinLogging.logger {}

/**
 * Application principale BoraStock
 */
class BoraStockApplication(
    private val container: DependencyContainer
) {
    
    suspend fun start() {
        logger.info { "🚀 Démarrage de BoraStock Application" }
        
        try {
            // Initialisation des services
            initializeServices()
            
            // Démarrage de l'interface CLI
            val cli = CliInterface(container)
            cli.start()
            
        } catch (e: Exception) {
            logger.error(e) { "❌ Erreur lors du démarrage de l'application" }
            throw e
        }
    }
    
    private suspend fun initializeServices() {
        logger.info { "🔧 Initialisation des services..." }
        
        // Ici on pourrait initialiser la base de données, les connexions, etc.
        // Pour l'instant, on utilise des repositories en mémoire
        
        logger.info { "✅ Services initialisés avec succès" }
    }
    
    suspend fun stop() {
        logger.info { "🛑 Arrêt de BoraStock Application" }
        // Nettoyage des ressources si nécessaire
    }
}