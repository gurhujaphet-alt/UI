package org.babetech.borastock.domain.service

import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.babetech.borastock.domain.model.*
import org.babetech.borastock.domain.repository.CategoryRepository
import org.babetech.borastock.domain.repository.ProductRepository
import org.babetech.borastock.domain.repository.SupplierRepository
import kotlin.test.*

class ProductServiceTest {
    
    private val productRepository = mockk<ProductRepository>()
    private val categoryRepository = mockk<CategoryRepository>()
    private val supplierRepository = mockk<SupplierRepository>()
    
    private val productService = ProductService(
        productRepository,
        categoryRepository,
        supplierRepository
    )
    
    private fun createTestCategory(): Category {
        return Category(CategoryId("CAT_001"), "Test Category")
    }
    
    private fun createTestProduct(): Product {
        val now = Clock.System.now()
        return Product(
            id = ProductId("PRD_001"),
            name = "Test Product",
            description = "A test product",
            category = createTestCategory(),
            price = Money(99.99),
            stock = Stock(currentQuantity = 50, minThreshold = 10, maxCapacity = 100),
            supplier = SupplierId("SUP_001"),
            createdAt = now,
            updatedAt = now
        )
    }
    
    @Test
    fun `should create product successfully`() = runTest {
        // Given
        val categoryId = CategoryId("CAT_001")
        val supplierId = SupplierId("SUP_001")
        val category = createTestCategory()
        
        coEvery { categoryRepository.exists(categoryId) } returns true
        coEvery { supplierRepository.exists(supplierId) } returns true
        coEvery { categoryRepository.findById(categoryId) } returns category
        coEvery { productRepository.save(any()) } returnsArgument 0
        
        // When
        val result = productService.createProduct(
            name = "Test Product",
            description = "A test product",
            categoryId = categoryId,
            price = Money(99.99),
            stock = Stock(currentQuantity = 50, minThreshold = 10, maxCapacity = 100),
            supplierId = supplierId
        )
        
        // Then
        assertTrue(result.isSuccess)
        val product = result.getOrThrow()
        assertEquals("Test Product", product.name)
        assertEquals("A test product", product.description)
        assertEquals(99.99, product.price.amount)
        
        coVerify { productRepository.save(any()) }
    }
    
    @Test
    fun `should fail to create product with non-existent category`() = runTest {
        // Given
        val categoryId = CategoryId("CAT_INVALID")
        val supplierId = SupplierId("SUP_001")
        
        coEvery { categoryRepository.exists(categoryId) } returns false
        coEvery { supplierRepository.exists(supplierId) } returns true
        
        // When
        val result = productService.createProduct(
            name = "Test Product",
            description = "A test product",
            categoryId = categoryId,
            price = Money(99.99),
            stock = Stock(currentQuantity = 50, minThreshold = 10, maxCapacity = 100),
            supplierId = supplierId
        )
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Catégorie introuvable", result.exceptionOrNull()?.message)
        
        coVerify(exactly = 0) { productRepository.save(any()) }
    }
    
    @Test
    fun `should update product successfully`() = runTest {
        // Given
        val productId = ProductId("PRD_001")
        val existingProduct = createTestProduct()
        val updatedProduct = existingProduct.copy(name = "Updated Product")
        
        coEvery { productRepository.findById(productId) } returns existingProduct
        coEvery { productRepository.save(any()) } returns updatedProduct
        
        // When
        val result = productService.updateProduct(
            id = productId,
            name = "Updated Product"
        )
        
        // Then
        assertTrue(result.isSuccess)
        val product = result.getOrThrow()
        assertEquals("Updated Product", product.name)
        
        coVerify { productRepository.save(any()) }
    }
    
    @Test
    fun `should fail to update non-existent product`() = runTest {
        // Given
        val productId = ProductId("PRD_INVALID")
        
        coEvery { productRepository.findById(productId) } returns null
        
        // When
        val result = productService.updateProduct(
            id = productId,
            name = "Updated Product"
        )
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Produit introuvable", result.exceptionOrNull()?.message)
        
        coVerify(exactly = 0) { productRepository.save(any()) }
    }
    
    @Test
    fun `should update stock successfully`() = runTest {
        // Given
        val productId = ProductId("PRD_001")
        val existingProduct = createTestProduct()
        val updatedProduct = existingProduct.updateStock(75)
        
        coEvery { productRepository.findById(productId) } returns existingProduct
        coEvery { productRepository.save(any()) } returns updatedProduct
        
        // When
        val result = productService.updateStock(productId, 75)
        
        // Then
        assertTrue(result.isSuccess)
        val product = result.getOrThrow()
        assertEquals(75, product.stock.currentQuantity)
        
        coVerify { productRepository.save(any()) }
    }
    
    @Test
    fun `should get stock summary correctly`() = runTest {
        // Given
        coEvery { productRepository.count() } returns 100
        coEvery { productRepository.countByStatus(StockStatus.IN_STOCK) } returns 70
        coEvery { productRepository.countByStatus(StockStatus.LOW_STOCK) } returns 20
        coEvery { productRepository.countByStatus(StockStatus.OUT_OF_STOCK) } returns 8
        coEvery { productRepository.countByStatus(StockStatus.OVERSTOCKED) } returns 2
        
        // When
        val summary = productService.getStockSummary()
        
        // Then
        assertEquals(100, summary.totalProducts)
        assertEquals(70, summary.inStock)
        assertEquals(20, summary.lowStock)
        assertEquals(8, summary.outOfStock)
        assertEquals(2, summary.overstocked)
        assertEquals(70.0, summary.healthyStockPercentage)
    }
}