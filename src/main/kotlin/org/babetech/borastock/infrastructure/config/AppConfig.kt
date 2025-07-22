package org.babetech.borastock.infrastructure.config

import kotlinx.serialization.Serializable

/**
 * Configuration de l'application
 */
@Serializable
data class AppConfig(
    val database: DatabaseConfig = DatabaseConfig(),
    val logging: LoggingConfig = LoggingConfig(),
    val application: ApplicationConfig = ApplicationConfig()
) {
    companion object {
        fun load(): AppConfig {
            // Pour l'instant, on retourne une configuration par défaut
            // Dans une vraie application, on chargerait depuis un fichier ou des variables d'environnement
            return AppConfig()
        }
    }
}

@Serializable
data class DatabaseConfig(
    val type: String = "memory", // memory, sqlite, postgresql, etc.
    val url: String = "",
    val username: String = "",
    val password: String = "",
    val maxConnections: Int = 10
)

@Serializable
data class LoggingConfig(
    val level: String = "INFO",
    val format: String = "text", // text, json
    val file: String? = null
)

@Serializable
data class ApplicationConfig(
    val name: String = "BoraStock",
    val version: String = "1.0.0",
    val environment: String = "development" // development, production, test
)