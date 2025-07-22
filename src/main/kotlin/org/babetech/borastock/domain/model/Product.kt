package org.babetech.borastock.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Entité métier représentant un produit
 */
@Serializable
data class Product(
    val id: ProductId,
    val name: String,
    val description: String? = null,
    val category: Category,
    val price: Money,
    val stock: Stock,
    val supplier: SupplierId,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(name.isNotBlank()) { "Le nom du produit ne peut pas être vide" }
        require(price.amount >= 0.0) { "Le prix ne peut pas être négatif" }
    }
    
    fun isLowStock(): Boolean = stock.isLow()
    fun isOutOfStock(): Boolean = stock.isEmpty()
    fun isOverstocked(): Boolean = stock.isOverstocked()
    
    fun updateStock(newQuantity: Int): Product {
        return copy(
            stock = stock.updateQuantity(newQuantity),
            updatedAt = kotlinx.datetime.Clock.System.now()
        )
    }
    
    fun updatePrice(newPrice: Money): Product {
        return copy(
            price = newPrice,
            updatedAt = kotlinx.datetime.Clock.System.now()
        )
    }
}

@Serializable
@JvmInline
value class ProductId(val value: String) {
    init {
        require(value.isNotBlank()) { "L'ID du produit ne peut pas être vide" }
    }
    
    companion object {
        fun generate(): ProductId = ProductId("PRD_${System.currentTimeMillis()}")
    }
}

@Serializable
data class Category(
    val id: CategoryId,
    val name: String,
    val description: String? = null
) {
    init {
        require(name.isNotBlank()) { "Le nom de la catégorie ne peut pas être vide" }
    }
}

@Serializable
@JvmInline
value class CategoryId(val value: String) {
    companion object {
        fun generate(): CategoryId = CategoryId("CAT_${System.currentTimeMillis()}")
    }
}

@Serializable
data class Money(
    val amount: Double,
    val currency: String = "EUR"
) {
    init {
        require(amount >= 0.0) { "Le montant ne peut pas être négatif" }
        require(currency.isNotBlank()) { "La devise ne peut pas être vide" }
    }
    
    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Les devises doivent être identiques" }
        return Money(amount + other.amount, currency)
    }
    
    operator fun times(quantity: Int): Money {
        return Money(amount * quantity, currency)
    }
    
    override fun toString(): String = "$amount $currency"
}

@Serializable
data class Stock(
    val currentQuantity: Int,
    val minThreshold: Int,
    val maxCapacity: Int
) {
    init {
        require(currentQuantity >= 0) { "La quantité ne peut pas être négative" }
        require(minThreshold >= 0) { "Le seuil minimum ne peut pas être négatif" }
        require(maxCapacity > 0) { "La capacité maximale doit être positive" }
        require(minThreshold <= maxCapacity) { "Le seuil minimum ne peut pas être supérieur à la capacité maximale" }
    }
    
    fun isEmpty(): Boolean = currentQuantity == 0
    fun isLow(): Boolean = currentQuantity <= minThreshold && currentQuantity > 0
    fun isOverstocked(): Boolean = currentQuantity > maxCapacity
    fun isNormal(): Boolean = currentQuantity in (minThreshold + 1)..maxCapacity
    
    fun updateQuantity(newQuantity: Int): Stock {
        require(newQuantity >= 0) { "La nouvelle quantité ne peut pas être négative" }
        return copy(currentQuantity = newQuantity)
    }
    
    fun addQuantity(quantity: Int): Stock {
        require(quantity > 0) { "La quantité à ajouter doit être positive" }
        return updateQuantity(currentQuantity + quantity)
    }
    
    fun removeQuantity(quantity: Int): Stock {
        require(quantity > 0) { "La quantité à retirer doit être positive" }
        require(quantity <= currentQuantity) { "Impossible de retirer plus que la quantité disponible" }
        return updateQuantity(currentQuantity - quantity)
    }
    
    fun getStatus(): StockStatus = when {
        isEmpty() -> StockStatus.OUT_OF_STOCK
        isLow() -> StockStatus.LOW_STOCK
        isOverstocked() -> StockStatus.OVERSTOCKED
        else -> StockStatus.IN_STOCK
    }
}

enum class StockStatus(val label: String) {
    IN_STOCK("En stock"),
    LOW_STOCK("Stock faible"),
    OUT_OF_STOCK("Rupture de stock"),
    OVERSTOCKED("Surstock")
}