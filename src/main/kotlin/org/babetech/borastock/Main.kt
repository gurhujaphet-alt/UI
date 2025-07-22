package org.babetech.borastock

import org.babetech.borastock.application.BoraStockApplication
import org.babetech.borastock.infrastructure.config.AppConfig
import org.babetech.borastock.infrastructure.di.DependencyContainer

/**
 * Point d'entrée principal de l'application BoraStock
 */
suspend fun main() {
    println("🚀 Démarrage de BoraStock...")
    
    try {
        // Configuration de l'application
        val config = AppConfig.load()
        
        // Initialisation du conteneur de dépendances
        val container = DependencyContainer(config)
        
        // Création et démarrage de l'application
        val app = BoraStockApplication(container)
        app.start()
        
    } catch (e: Exception) {
        println("❌ Erreur lors du démarrage: ${e.message}")
        e.printStackTrace()
    }
}