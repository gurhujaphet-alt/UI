package org.babetech.borastock.infrastructure.repository.memory

import org.babetech.borastock.domain.model.*
import org.babetech.borastock.domain.repository.SupplierRepository
import java.util.concurrent.ConcurrentHashMap

/**
 * Implémentation en mémoire du repository des fournisseurs
 */
class InMemorySupplierRepository : SupplierRepository {
    private val suppliers = ConcurrentHashMap<SupplierId, Supplier>()
    
    init {
        // Ajouter quelques fournisseurs par défaut
        val now = kotlinx.datetime.Clock.System.now()
        val defaultSuppliers = listOf(
            Supplier(
                id = SupplierId("SUP_001"),
                name = "Apple Inc.",
                contactInfo = ContactInfo(email = "contact@apple.com", website = "https://apple.com"),
                address = Address("One Apple Park Way", "Cupertino", "95014", "USA"),
                createdAt = now,
                updatedAt = now
            ),
            Supplier(
                id = SupplierId("SUP_002"),
                name = "Samsung",
                contactInfo = ContactInfo(email = "info@samsung.com", website = "https://samsung.com"),
                createdAt = now,
                updatedAt = now
            ),
            Supplier(
                id = SupplierId("SUP_003"),
                name = "Dell",
                contactInfo = ContactInfo(email = "support@dell.com", phone = "+1-800-DELL"),
                createdAt = now,
                updatedAt = now
            )
        )
        
        defaultSuppliers.forEach { supplier ->
            suppliers[supplier.id] = supplier
        }
    }
    
    override suspend fun findById(id: SupplierId): Supplier? = suppliers[id]
    
    override suspend fun findAll(): List<Supplier> = suppliers.values.toList()
    
    override suspend fun findActive(): List<Supplier> = 
        suppliers.values.filter { it.isActive }
    
    override suspend fun findByName(name: String): List<Supplier> = 
        suppliers.values.filter { it.name.contains(name, ignoreCase = true) }
    
    override suspend fun search(query: String): List<Supplier> = 
        suppliers.values.filter { supplier ->
            supplier.name.contains(query, ignoreCase = true) ||
            supplier.contactInfo.email?.contains(query, ignoreCase = true) == true
        }
    
    override suspend fun save(supplier: Supplier): Supplier {
        suppliers[supplier.id] = supplier
        return supplier
    }
    
    override suspend fun delete(id: SupplierId): Boolean = 
        suppliers.remove(id) != null
    
    override suspend fun exists(id: SupplierId): Boolean = 
        suppliers.containsKey(id)
    
    override suspend fun count(): Long = suppliers.size.toLong()
    
    override suspend fun countActive(): Long = 
        suppliers.values.count { it.isActive }.toLong()
}