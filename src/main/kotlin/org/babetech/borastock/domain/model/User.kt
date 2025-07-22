package org.babetech.borastock.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Entité métier représentant un utilisateur
 */
@Serializable
data class User(
    val id: UserId,
    val email: String,
    val name: String,
    val role: UserRole,
    val isActive: Boolean = true,
    val createdAt: Instant,
    val lastLoginAt: Instant? = null
) {
    init {
        require(email.contains("@")) { "Format d'email invalide" }
        require(name.isNotBlank()) { "Le nom ne peut pas être vide" }
    }
    
    fun hasPermission(permission: Permission): Boolean = role.hasPermission(permission)
    
    fun updateLastLogin(): User = copy(
        lastLoginAt = kotlinx.datetime.Clock.System.now()
    )
    
    fun activate(): User = copy(isActive = true)
    fun deactivate(): User = copy(isActive = false)
}

enum class UserRole(val label: String, val permissions: Set<Permission>) {
    ADMIN("Administrateur", Permission.values().toSet()),
    MANAGER("Gestionnaire", setOf(
        Permission.READ_PRODUCTS,
        Permission.WRITE_PRODUCTS,
        Permission.READ_SUPPLIERS,
        Permission.WRITE_SUPPLIERS,
        Permission.READ_STOCK,
        Permission.WRITE_STOCK,
        Permission.READ_MOVEMENTS,
        Permission.WRITE_MOVEMENTS
    )),
    EMPLOYEE("Employé", setOf(
        Permission.READ_PRODUCTS,
        Permission.READ_SUPPLIERS,
        Permission.READ_STOCK,
        Permission.WRITE_STOCK,
        Permission.READ_MOVEMENTS,
        Permission.WRITE_MOVEMENTS
    )),
    VIEWER("Observateur", setOf(
        Permission.READ_PRODUCTS,
        Permission.READ_SUPPLIERS,
        Permission.READ_STOCK,
        Permission.READ_MOVEMENTS
    ));
    
    fun hasPermission(permission: Permission): Boolean = permissions.contains(permission)
}

enum class Permission {
    READ_PRODUCTS,
    WRITE_PRODUCTS,
    READ_SUPPLIERS,
    WRITE_SUPPLIERS,
    READ_STOCK,
    WRITE_STOCK,
    READ_MOVEMENTS,
    WRITE_MOVEMENTS,
    READ_USERS,
    WRITE_USERS,
    READ_ANALYTICS,
    SYSTEM_CONFIG
}