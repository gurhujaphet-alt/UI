package org.babetech.borastock.infrastructure.di

import org.babetech.borastock.domain.repository.*
import org.babetech.borastock.domain.service.*
import org.babetech.borastock.infrastructure.config.AppConfig
import org.babetech.borastock.infrastructure.repository.memory.*

/**
 * Conteneur de dépendances simple pour l'injection de dépendances
 */
class DependencyContainer(
    private val config: AppConfig
) {
    
    // Repositories
    val productRepository: ProductRepository by lazy { InMemoryProductRepository() }
    val categoryRepository: CategoryRepository by lazy { InMemoryCategoryRepository() }
    val supplierRepository: SupplierRepository by lazy { InMemorySupplierRepository() }
    val stockMovementRepository: StockMovementRepository by lazy { InMemoryStockMovementRepository() }
    val userRepository: UserRepository by lazy { InMemoryUserRepository() }
    
    // Services
    val productService: ProductService by lazy { 
        ProductService(productRepository, categoryRepository, supplierRepository) 
    }
    
    val supplierService: SupplierService by lazy { 
        SupplierService(supplierRepository) 
    }
    
    val stockMovementService: StockMovementService by lazy { 
        StockMovementService(stockMovementRepository, productRepository, productService) 
    }
}