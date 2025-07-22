package org.babetech.borastock.domain.repository

import kotlinx.datetime.Instant
import org.babetech.borastock.domain.model.*

/**
 * Interface du repository pour les mouvements de stock
 */
interface StockMovementRepository {
    suspend fun findById(id: MovementId): StockMovement?
    suspend fun findAll(): List<StockMovement>
    suspend fun findByProduct(productId: ProductId): List<StockMovement>
    suspend fun findByType(type: MovementType): List<StockMovement>
    suspend fun findByDateRange(start: Instant, end: Instant): List<StockMovement>
    suspend fun findByUser(userId: UserId): List<StockMovement>
    suspend fun findRecent(limit: Int = 50): List<StockMovement>
    
    suspend fun save(movement: StockMovement): StockMovement
    suspend fun delete(id: MovementId): Boolean
    
    suspend fun count(): Long
    suspend fun countByType(type: MovementType): Long
    suspend fun countByDateRange(start: Instant, end: Instant): Long
}