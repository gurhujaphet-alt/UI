package org.babetech.borastock.domain.model

import kotlin.test.*

class StockTest {
    
    @Test
    fun `should create valid stock`() {
        val stock = Stock(currentQuantity = 50, minThreshold = 10, maxCapacity = 100)
        
        assertEquals(50, stock.currentQuantity)
        assertEquals(10, stock.minThreshold)
        assertEquals(100, stock.maxCapacity)
    }
    
    @Test
    fun `should fail with negative quantity`() {
        assertFailsWith<IllegalArgumentException> {
            Stock(currentQuantity = -1, minThreshold = 10, maxCapacity = 100)
        }
    }
    
    @Test
    fun `should fail with negative min threshold`() {
        assertFailsWith<IllegalArgumentException> {
            Stock(currentQuantity = 50, minThreshold = -1, maxCapacity = 100)
        }
    }
    
    @Test
    fun `should fail with zero max capacity`() {
        assertFailsWith<IllegalArgumentException> {
            Stock(currentQuantity = 50, minThreshold = 10, maxCapacity = 0)
        }
    }
    
    @Test
    fun `should fail when min threshold exceeds max capacity`() {
        assertFailsWith<IllegalArgumentException> {
            Stock(currentQuantity = 50, minThreshold = 150, maxCapacity = 100)
        }
    }
    
    @Test
    fun `should detect empty stock`() {
        val stock = Stock(currentQuantity = 0, minThreshold = 10, maxCapacity = 100)
        assertTrue(stock.isEmpty())
        assertEquals(StockStatus.OUT_OF_STOCK, stock.getStatus())
    }
    
    @Test
    fun `should detect low stock`() {
        val stock = Stock(currentQuantity = 5, minThreshold = 10, maxCapacity = 100)
        assertTrue(stock.isLow())
        assertEquals(StockStatus.LOW_STOCK, stock.getStatus())
    }
    
    @Test
    fun `should detect overstocked`() {
        val stock = Stock(currentQuantity = 150, minThreshold = 10, maxCapacity = 100)
        assertTrue(stock.isOverstocked())
        assertEquals(StockStatus.OVERSTOCKED, stock.getStatus())
    }
    
    @Test
    fun `should detect normal stock`() {
        val stock = Stock(currentQuantity = 50, minThreshold = 10, maxCapacity = 100)
        assertTrue(stock.isNormal())
        assertEquals(StockStatus.IN_STOCK, stock.getStatus())
    }
    
    @Test
    fun `should add quantity correctly`() {
        val stock = Stock(currentQuantity = 50, minThreshold = 10, maxCapacity = 100)
        val updatedStock = stock.addQuantity(25)
        
        assertEquals(75, updatedStock.currentQuantity)
    }
    
    @Test
    fun `should remove quantity correctly`() {
        val stock = Stock(currentQuantity = 50, minThreshold = 10, maxCapacity = 100)
        val updatedStock = stock.removeQuantity(20)
        
        assertEquals(30, updatedStock.currentQuantity)
    }
    
    @Test
    fun `should fail when removing more than available`() {
        val stock = Stock(currentQuantity = 50, minThreshold = 10, maxCapacity = 100)
        
        assertFailsWith<IllegalArgumentException> {
            stock.removeQuantity(60)
        }
    }
    
    @Test
    fun `should fail when adding zero or negative quantity`() {
        val stock = Stock(currentQuantity = 50, minThreshold = 10, maxCapacity = 100)
        
        assertFailsWith<IllegalArgumentException> {
            stock.addQuantity(0)
        }
        
        assertFailsWith<IllegalArgumentException> {
            stock.addQuantity(-5)
        }
    }
}