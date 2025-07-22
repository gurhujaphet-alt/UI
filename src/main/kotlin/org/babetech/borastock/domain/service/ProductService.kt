package org.babetech.borastock.domain.service

import org.babetech.borastock.domain.model.*
import org.babetech.borastock.domain.repository.ProductRepository
import org.babetech.borastock.domain.repository.CategoryRepository
import org.babetech.borastock.domain.repository.SupplierRepository

/**
 * Service métier pour la gestion des produits
 */
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val supplierRepository: SupplierRepository
) {
    
    suspend fun createProduct(
        name: String,
        description: String?,
        categoryId: CategoryId,
        price: Money,
        stock: Stock,
        supplierId: SupplierId
    ): Result<Product> = runCatching {
        // Vérifications métier
        require(categoryRepository.exists(categoryId)) { "Catégorie introuvable" }
        require(supplierRepository.exists(supplierId)) { "Fournisseur introuvable" }
        
        val now = kotlinx.datetime.Clock.System.now()
        val category = categoryRepository.findById(categoryId)!!
        
        val product = Product(
            id = ProductId.generate(),
            name = name,
            description = description,
            category = category,
            price = price,
            stock = stock,
            supplier = supplierId,
            createdAt = now,
            updatedAt = now
        )
        
        productRepository.save(product)
    }
    
    suspend fun updateProduct(
        id: ProductId,
        name: String? = null,
        description: String? = null,
        price: Money? = null
    ): Result<Product> = runCatching {
        val existingProduct = productRepository.findById(id)
            ?: throw IllegalArgumentException("Produit introuvable")
        
        val updatedProduct = existingProduct.copy(
            name = name ?: existingProduct.name,
            description = description ?: existingProduct.description,
            price = price ?: existingProduct.price,
            updatedAt = kotlinx.datetime.Clock.System.now()
        )
        
        productRepository.save(updatedProduct)
    }
    
    suspend fun updateStock(id: ProductId, newQuantity: Int): Result<Product> = runCatching {
        val product = productRepository.findById(id)
            ?: throw IllegalArgumentException("Produit introuvable")
        
        val updatedProduct = product.updateStock(newQuantity)
        productRepository.save(updatedProduct)
    }
    
    suspend fun deleteProduct(id: ProductId): Result<Boolean> = runCatching {
        require(productRepository.exists(id)) { "Produit introuvable" }
        productRepository.delete(id)
    }
    
    suspend fun getProduct(id: ProductId): Product? = productRepository.findById(id)
    
    suspend fun getAllProducts(): List<Product> = productRepository.findAll()
    
    suspend fun getProductsByCategory(categoryId: CategoryId): List<Product> = 
        productRepository.findByCategory(categoryId)
    
    suspend fun getProductsBySupplier(supplierId: SupplierId): List<Product> = 
        productRepository.findBySupplier(supplierId)
    
    suspend fun getLowStockProducts(): List<Product> = productRepository.findLowStockProducts()
    
    suspend fun getProductsByStatus(status: StockStatus): List<Product> = 
        productRepository.findByStatus(status)
    
    suspend fun searchProducts(query: String): List<Product> = productRepository.search(query)
    
    suspend fun getStockSummary(): StockSummary {
        val totalProducts = productRepository.count()
        val inStock = productRepository.countByStatus(StockStatus.IN_STOCK)
        val lowStock = productRepository.countByStatus(StockStatus.LOW_STOCK)
        val outOfStock = productRepository.countByStatus(StockStatus.OUT_OF_STOCK)
        val overstocked = productRepository.countByStatus(StockStatus.OVERSTOCKED)
        
        return StockSummary(
            totalProducts = totalProducts,
            inStock = inStock,
            lowStock = lowStock,
            outOfStock = outOfStock,
            overstocked = overstocked
        )
    }
}

data class StockSummary(
    val totalProducts: Long,
    val inStock: Long,
    val lowStock: Long,
    val outOfStock: Long,
    val overstocked: Long
) {
    val totalValue: Long = inStock + lowStock + outOfStock + overstocked
    val healthyStockPercentage: Double = if (totalProducts > 0) (inStock.toDouble() / totalProducts) * 100 else 0.0
}