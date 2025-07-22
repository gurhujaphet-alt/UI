package org.babetech.borastock.infrastructure.repository.memory

import org.babetech.borastock.domain.model.*
import org.babetech.borastock.domain.repository.ProductRepository
import java.util.concurrent.ConcurrentHashMap

/**
 * Implémentation en mémoire du repository des produits
 */
class InMemoryProductRepository : ProductRepository {
    private val products = ConcurrentHashMap<ProductId, Product>()
    
    override suspend fun findById(id: ProductId): Product? = products[id]
    
    override suspend fun findAll(): List<Product> = products.values.toList()
    
    override suspend fun findByCategory(categoryId: CategoryId): List<Product> = 
        products.values.filter { it.category.id == categoryId }
    
    override suspend fun findBySupplier(supplierId: SupplierId): List<Product> = 
        products.values.filter { it.supplier == supplierId }
    
    override suspend fun findByStatus(status: StockStatus): List<Product> = 
        products.values.filter { it.stock.getStatus() == status }
    
    override suspend fun findLowStockProducts(): List<Product> = 
        products.values.filter { it.isLowStock() }
    
    override suspend fun search(query: String): List<Product> = 
        products.values.filter { product ->
            product.name.contains(query, ignoreCase = true) ||
            product.description?.contains(query, ignoreCase = true) == true ||
            product.category.name.contains(query, ignoreCase = true)
        }
    
    override suspend fun save(product: Product): Product {
        products[product.id] = product
        return product
    }
    
    override suspend fun delete(id: ProductId): Boolean = 
        products.remove(id) != null
    
    override suspend fun exists(id: ProductId): Boolean = 
        products.containsKey(id)
    
    override suspend fun count(): Long = products.size.toLong()
    
    override suspend fun countByStatus(status: StockStatus): Long = 
        products.values.count { it.stock.getStatus() == status }.toLong()
}