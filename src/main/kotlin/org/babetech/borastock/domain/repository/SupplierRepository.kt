package org.babetech.borastock.domain.repository

import org.babetech.borastock.domain.model.Supplier
import org.babetech.borastock.domain.model.SupplierId

/**
 * Interface du repository pour les fournisseurs
 */
interface SupplierRepository {
    suspend fun findById(id: SupplierId): Supplier?
    suspend fun findAll(): List<Supplier>
    suspend fun findActive(): List<Supplier>
    suspend fun findByName(name: String): List<Supplier>
    suspend fun search(query: String): List<Supplier>
    
    suspend fun save(supplier: Supplier): Supplier
    suspend fun delete(id: SupplierId): Boolean
    suspend fun exists(id: SupplierId): Boolean
    
    suspend fun count(): Long
    suspend fun countActive(): Long
}