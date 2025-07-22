package org.babetech.borastock.domain.repository

import org.babetech.borastock.domain.model.*

/**
 * Interface du repository pour les produits
 */
interface ProductRepository {
    suspend fun findById(id: ProductId): Product?
    suspend fun findAll(): List<Product>
    suspend fun findByCategory(categoryId: CategoryId): List<Product>
    suspend fun findBySupplier(supplierId: SupplierId): List<Product>
    suspend fun findByStatus(status: StockStatus): List<Product>
    suspend fun findLowStockProducts(): List<Product>
    suspend fun search(query: String): List<Product>
    
    suspend fun save(product: Product): Product
    suspend fun delete(id: ProductId): Boolean
    suspend fun exists(id: ProductId): Boolean
    
    suspend fun count(): Long
    suspend fun countByStatus(status: StockStatus): Long
}

/**
 * Interface du repository pour les catégories
 */
interface CategoryRepository {
    suspend fun findById(id: CategoryId): Category?
    suspend fun findAll(): List<Category>
    suspend fun findByName(name: String): Category?
    
    suspend fun save(category: Category): Category
    suspend fun delete(id: CategoryId): Boolean
    suspend fun exists(id: CategoryId): Boolean
}