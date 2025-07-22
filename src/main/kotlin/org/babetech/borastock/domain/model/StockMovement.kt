package org.babetech.borastock.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Entité métier représentant un mouvement de stock
 */
@Serializable
data class StockMovement(
    val id: MovementId,
    val productId: ProductId,
    val type: MovementType,
    val quantity: Int,
    val reason: String,
    val performedBy: UserId,
    val timestamp: Instant,
    val reference: String? = null
) {
    init {
        require(quantity > 0) { "La quantité doit être positive" }
        require(reason.isNotBlank()) { "La raison ne peut pas être vide" }
    }
    
    fun isEntry(): Boolean = type == MovementType.ENTRY
    fun isExit(): Boolean = type == MovementType.EXIT
}

@Serializable
@JvmInline
value class MovementId(val value: String) {
    companion object {
        fun generate(): MovementId = MovementId("MOV_${System.currentTimeMillis()}")
    }
}

@Serializable
@JvmInline
value class UserId(val value: String) {
    companion object {
        fun generate(): UserId = UserId("USR_${System.currentTimeMillis()}")
    }
}

enum class MovementType(val label: String) {
    ENTRY("Entrée"),
    EXIT("Sortie")
}