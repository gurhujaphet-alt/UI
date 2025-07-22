package org.babetech.borastock.domain.service

import kotlinx.datetime.Instant
import org.babetech.borastock.domain.model.*
import org.babetech.borastock.domain.repository.StockMovementRepository
import org.babetech.borastock.domain.repository.ProductRepository

/**
 * Service métier pour la gestion des mouvements de stock
 */
class StockMovementService(
    private val movementRepository: StockMovementRepository,
    private val productRepository: ProductRepository,
    private val productService: ProductService
) {
    
    suspend fun recordEntry(
        productId: ProductId,
        quantity: Int,
        reason: String,
        performedBy: UserId,
        reference: String? = null
    ): Result<StockMovement> = runCatching {
        require(quantity > 0) { "La quantité doit être positive" }
        require(productRepository.exists(productId)) { "Produit introuvable" }
        
        // Créer le mouvement
        val movement = StockMovement(
            id = MovementId.generate(),
            productId = productId,
            type = MovementType.ENTRY,
            quantity = quantity,
            reason = reason,
            performedBy = performedBy,
            timestamp = kotlinx.datetime.Clock.System.now(),
            reference = reference
        )
        
        // Sauvegarder le mouvement
        val savedMovement = movementRepository.save(movement)
        
        // Mettre à jour le stock du produit
        val product = productRepository.findById(productId)!!
        val newQuantity = product.stock.currentQuantity + quantity
        productService.updateStock(productId, newQuantity)
        
        savedMovement
    }
    
    suspend fun recordExit(
        productId: ProductId,
        quantity: Int,
        reason: String,
        performedBy: UserId,
        reference: String? = null
    ): Result<StockMovement> = runCatching {
        require(quantity > 0) { "La quantité doit être positive" }
        
        val product = productRepository.findById(productId)
            ?: throw IllegalArgumentException("Produit introuvable")
        
        require(product.stock.currentQuantity >= quantity) { 
            "Stock insuffisant. Disponible: ${product.stock.currentQuantity}, Demandé: $quantity" 
        }
        
        // Créer le mouvement
        val movement = StockMovement(
            id = MovementId.generate(),
            productId = productId,
            type = MovementType.EXIT,
            quantity = quantity,
            reason = reason,
            performedBy = performedBy,
            timestamp = kotlinx.datetime.Clock.System.now(),
            reference = reference
        )
        
        // Sauvegarder le mouvement
        val savedMovement = movementRepository.save(movement)
        
        // Mettre à jour le stock du produit
        val newQuantity = product.stock.currentQuantity - quantity
        productService.updateStock(productId, newQuantity)
        
        savedMovement
    }
    
    suspend fun getMovement(id: MovementId): StockMovement? = movementRepository.findById(id)
    
    suspend fun getAllMovements(): List<StockMovement> = movementRepository.findAll()
    
    suspend fun getMovementsByProduct(productId: ProductId): List<StockMovement> = 
        movementRepository.findByProduct(productId)
    
    suspend fun getMovementsByType(type: MovementType): List<StockMovement> = 
        movementRepository.findByType(type)
    
    suspend fun getMovementsByDateRange(start: Instant, end: Instant): List<StockMovement> = 
        movementRepository.findByDateRange(start, end)
    
    suspend fun getRecentMovements(limit: Int = 50): List<StockMovement> = 
        movementRepository.findRecent(limit)
    
    suspend fun getMovementsByUser(userId: UserId): List<StockMovement> = 
        movementRepository.findByUser(userId)
    
    suspend fun getMovementSummary(): MovementSummary {
        val totalMovements = movementRepository.count()
        val entries = movementRepository.countByType(MovementType.ENTRY)
        val exits = movementRepository.countByType(MovementType.EXIT)
        
        return MovementSummary(
            totalMovements = totalMovements,
            entries = entries,
            exits = exits
        )
    }
    
    suspend fun getMovementSummaryByDateRange(start: Instant, end: Instant): MovementSummary {
        val movements = movementRepository.findByDateRange(start, end)
        val entries = movements.count { it.type == MovementType.ENTRY }.toLong()
        val exits = movements.count { it.type == MovementType.EXIT }.toLong()
        
        return MovementSummary(
            totalMovements = movements.size.toLong(),
            entries = entries,
            exits = exits
        )
    }
}

data class MovementSummary(
    val totalMovements: Long,
    val entries: Long,
    val exits: Long
) {
    val netMovement: Long = entries - exits
}