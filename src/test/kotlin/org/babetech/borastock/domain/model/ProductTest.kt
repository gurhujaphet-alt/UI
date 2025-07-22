package org.babetech.borastock.domain.model

import kotlinx.datetime.Clock
import kotlin.test.*

class ProductTest {
    
    private fun createTestProduct(): Product {
        val now = Clock.System.now()
        return Product(
            id = ProductId("TEST_001"),
            name = "Test Product",
            description = "A test product",
            category = Category(CategoryId("CAT_001"), "Test Category"),
            price = Money(99.99),
            stock = Stock(currentQuantity = 50, minThreshold = 10, maxCapacity = 100),
            supplier = SupplierId("SUP_001"),
            createdAt = now,
            updatedAt = now
        )
    }
    
    @Test
    fun `should create valid product`() {
        val product = createTestProduct()
        
        assertEquals("Test Product", product.name)
        assertEquals(99.99, product.price.amount)
        assertEquals(50, product.stock.currentQuantity)
    }
    
    @Test
    fun `should fail with empty name`() {
        assertFailsWith<IllegalArgumentException> {
            createTestProduct().copy(name = "")
        }
    }
    
    @Test
    fun `should fail with negative price`() {
        assertFailsWith<IllegalArgumentException> {
            createTestProduct().copy(price = Money(-10.0))
        }
    }
    
    @Test
    fun `should detect low stock correctly`() {
        val product = createTestProduct().copy(
            stock = Stock(currentQuantity = 5, minThreshold = 10, maxCapacity = 100)
        )
        
        assertTrue(product.isLowStock())
        assertFalse(product.isOutOfStock())
        assertFalse(product.isOverstocked())
    }
    
    @Test
    fun `should detect out of stock correctly`() {
        val product = createTestProduct().copy(
            stock = Stock(currentQuantity = 0, minThreshold = 10, maxCapacity = 100)
        )
        
        assertFalse(product.isLowStock())
        assertTrue(product.isOutOfStock())
        assertFalse(product.isOverstocked())
    }
    
    @Test
    fun `should detect overstocked correctly`() {
        val product = createTestProduct().copy(
            stock = Stock(currentQuantity = 150, minThreshold = 10, maxCapacity = 100)
        )
        
        assertFalse(product.isLowStock())
        assertFalse(product.isOutOfStock())
        assertTrue(product.isOverstocked())
    }
    
    @Test
    fun `should update stock correctly`() {
        val product = createTestProduct()
        val updatedProduct = product.updateStock(75)
        
        assertEquals(75, updatedProduct.stock.currentQuantity)
        assertTrue(updatedProduct.updatedAt > product.updatedAt)
    }
    
    @Test
    fun `should update price correctly`() {
        val product = createTestProduct()
        val newPrice = Money(149.99)
        val updatedProduct = product.updatePrice(newPrice)
        
        assertEquals(149.99, updatedProduct.price.amount)
        assertTrue(updatedProduct.updatedAt > product.updatedAt)
    }
}