package org.babetech.borastock.domain.service

import org.babetech.borastock.domain.model.*
import org.babetech.borastock.domain.repository.SupplierRepository

/**
 * Service métier pour la gestion des fournisseurs
 */
class SupplierService(
    private val supplierRepository: SupplierRepository
) {
    
    suspend fun createSupplier(
        name: String,
        contactInfo: ContactInfo,
        address: Address? = null
    ): Result<Supplier> = runCatching {
        require(contactInfo.hasContact()) { "Au moins un moyen de contact est requis" }
        
        val now = kotlinx.datetime.Clock.System.now()
        val supplier = Supplier(
            id = SupplierId.generate(),
            name = name,
            contactInfo = contactInfo,
            address = address,
            isActive = true,
            createdAt = now,
            updatedAt = now
        )
        
        supplierRepository.save(supplier)
    }
    
    suspend fun updateSupplier(
        id: SupplierId,
        name: String? = null,
        contactInfo: ContactInfo? = null,
        address: Address? = null
    ): Result<Supplier> = runCatching {
        val existingSupplier = supplierRepository.findById(id)
            ?: throw IllegalArgumentException("Fournisseur introuvable")
        
        val updatedSupplier = existingSupplier.copy(
            name = name ?: existingSupplier.name,
            contactInfo = contactInfo ?: existingSupplier.contactInfo,
            address = address ?: existingSupplier.address,
            updatedAt = kotlinx.datetime.Clock.System.now()
        )
        
        supplierRepository.save(updatedSupplier)
    }
    
    suspend fun activateSupplier(id: SupplierId): Result<Supplier> = runCatching {
        val supplier = supplierRepository.findById(id)
            ?: throw IllegalArgumentException("Fournisseur introuvable")
        
        val activatedSupplier = supplier.activate()
        supplierRepository.save(activatedSupplier)
    }
    
    suspend fun deactivateSupplier(id: SupplierId): Result<Supplier> = runCatching {
        val supplier = supplierRepository.findById(id)
            ?: throw IllegalArgumentException("Fournisseur introuvable")
        
        val deactivatedSupplier = supplier.deactivate()
        supplierRepository.save(deactivatedSupplier)
    }
    
    suspend fun deleteSupplier(id: SupplierId): Result<Boolean> = runCatching {
        require(supplierRepository.exists(id)) { "Fournisseur introuvable" }
        supplierRepository.delete(id)
    }
    
    suspend fun getSupplier(id: SupplierId): Supplier? = supplierRepository.findById(id)
    
    suspend fun getAllSuppliers(): List<Supplier> = supplierRepository.findAll()
    
    suspend fun getActiveSuppliers(): List<Supplier> = supplierRepository.findActive()
    
    suspend fun searchSuppliers(query: String): List<Supplier> = supplierRepository.search(query)
    
    suspend fun getSuppliersByName(name: String): List<Supplier> = supplierRepository.findByName(name)
    
    suspend fun getSupplierSummary(): SupplierSummary {
        val totalSuppliers = supplierRepository.count()
        val activeSuppliers = supplierRepository.countActive()
        val inactiveSuppliers = totalSuppliers - activeSuppliers
        
        return SupplierSummary(
            totalSuppliers = totalSuppliers,
            activeSuppliers = activeSuppliers,
            inactiveSuppliers = inactiveSuppliers
        )
    }
}

data class SupplierSummary(
    val totalSuppliers: Long,
    val activeSuppliers: Long,
    val inactiveSuppliers: Long
) {
    val activePercentage: Double = if (totalSuppliers > 0) (activeSuppliers.toDouble() / totalSuppliers) * 100 else 0.0
}