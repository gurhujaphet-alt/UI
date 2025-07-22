package org.babetech.borastock.infrastructure.repository.memory

import kotlinx.datetime.Instant
import org.babetech.borastock.domain.model.*
import org.babetech.borastock.domain.repository.StockMovementRepository
import java.util.concurrent.ConcurrentHashMap

/**
 * Implémentation en mémoire du repository des mouvements de stock
 */
class InMemoryStockMovementRepository : StockMovementRepository {
    private val movements = ConcurrentHashMap<MovementId, StockMovement>()
    
    override suspend fun findById(id: MovementId): StockMovement? = movements[id]
    
    override suspend fun findAll(): List<StockMovement> = 
        movements.values.sortedByDescending { it.timestamp }
    
    override suspend fun findByProduct(productId: ProductId): List<StockMovement> = 
        movements.values.filter { it.productId == productId }
            .sortedByDescending { it.timestamp }
    
    override suspend fun findByType(type: MovementType): List<StockMovement> = 
        movements.values.filter { it.type == type }
            .sortedByDescending { it.timestamp }
    
    override suspend fun findByDateRange(start: Instant, end: Instant): List<StockMovement> = 
        movements.values.filter { it.timestamp >= start && it.timestamp <= end }
            .sortedByDescending { it.timestamp }
    
    override suspend fun findByUser(userId: UserId): List<StockMovement> = 
        movements.values.filter { it.performedBy == userId }
            .sortedByDescending { it.timestamp }
    
    override suspend fun findRecent(limit: Int): List<StockMovement> = 
        movements.values.sortedByDescending { it.timestamp }.take(limit)
    
    override suspend fun save(movement: StockMovement): StockMovement {
        movements[movement.id] = movement
        return movement
    }
    
    override suspend fun delete(id: MovementId): Boolean = 
        movements.remove(id) != null
    
    override suspend fun count(): Long = movements.size.toLong()
    
    override suspend fun countByType(type: MovementType): Long = 
        movements.values.count { it.type == type }.toLong()
    
    override suspend fun countByDateRange(start: Instant, end: Instant): Long = 
        movements.values.count { it.timestamp >= start && it.timestamp <= end }.toLong()
}