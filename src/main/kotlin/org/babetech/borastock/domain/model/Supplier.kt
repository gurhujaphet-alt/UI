package org.babetech.borastock.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Entité métier représentant un fournisseur
 */
@Serializable
data class Supplier(
    val id: SupplierId,
    val name: String,
    val contactInfo: ContactInfo,
    val address: Address? = null,
    val isActive: Boolean = true,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(name.isNotBlank()) { "Le nom du fournisseur ne peut pas être vide" }
    }
    
    fun activate(): Supplier = copy(
        isActive = true,
        updatedAt = kotlinx.datetime.Clock.System.now()
    )
    
    fun deactivate(): Supplier = copy(
        isActive = false,
        updatedAt = kotlinx.datetime.Clock.System.now()
    )
    
    fun updateContactInfo(newContactInfo: ContactInfo): Supplier = copy(
        contactInfo = newContactInfo,
        updatedAt = kotlinx.datetime.Clock.System.now()
    )
}

@Serializable
@JvmInline
value class SupplierId(val value: String) {
    init {
        require(value.isNotBlank()) { "L'ID du fournisseur ne peut pas être vide" }
    }
    
    companion object {
        fun generate(): SupplierId = SupplierId("SUP_${System.currentTimeMillis()}")
    }
}

@Serializable
data class ContactInfo(
    val email: String? = null,
    val phone: String? = null,
    val website: String? = null
) {
    init {
        email?.let { require(it.contains("@")) { "Format d'email invalide" } }
    }
    
    fun hasContact(): Boolean = !email.isNullOrBlank() || !phone.isNullOrBlank()
}

@Serializable
data class Address(
    val street: String,
    val city: String,
    val postalCode: String,
    val country: String
) {
    init {
        require(street.isNotBlank()) { "La rue ne peut pas être vide" }
        require(city.isNotBlank()) { "La ville ne peut pas être vide" }
        require(postalCode.isNotBlank()) { "Le code postal ne peut pas être vide" }
        require(country.isNotBlank()) { "Le pays ne peut pas être vide" }
    }
    
    override fun toString(): String = "$street, $city $postalCode, $country"
}