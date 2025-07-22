package org.babetech.borastock.application.cli.command

import org.babetech.borastock.domain.model.*
import org.babetech.borastock.domain.service.SupplierService

/**
 * Commandes pour la gestion des fournisseurs
 */
class SupplierCommand(
    private val supplierService: SupplierService
) : Command {
    
    override suspend fun execute(args: List<String>): Boolean {
        if (args.isEmpty()) {
            println("❌ Sous-commande manquante. Utilisez: supplier <list|create|show>")
            return true
        }
        
        when (args[0].lowercase()) {
            "list" -> listSuppliers()
            "create" -> createSupplier()
            "show" -> showSupplier(args.drop(1))
            "search" -> searchSuppliers(args.drop(1))
            else -> println("❌ Sous-commande inconnue: ${args[0]}")
        }
        
        return true
    }
    
    private suspend fun listSuppliers() {
        val suppliers = supplierService.getAllSuppliers()
        
        if (suppliers.isEmpty()) {
            println("🏢 Aucun fournisseur trouvé.")
            return
        }
        
        println("🏢 Liste des fournisseurs (${suppliers.size}):")
        println()
        println("%-15s %-30s %-25s %-10s".format("ID", "Nom", "Email", "Statut"))
        println("-".repeat(80))
        
        suppliers.forEach { supplier ->
            val status = if (supplier.isActive) "✅ Actif" else "❌ Inactif"
            
            println("%-15s %-30s %-25s %s".format(
                supplier.id.value.take(12) + "...",
                supplier.name.take(28),
                supplier.contactInfo.email?.take(23) ?: "N/A",
                status
            ))
        }
    }
    
    private suspend fun createSupplier() {
        println("🆕 Création d'un nouveau fournisseur")
        println()
        
        print("Nom du fournisseur: ")
        val name = readlnOrNull()?.trim() ?: return
        
        print("Email: ")
        val email = readlnOrNull()?.trim()?.takeIf { it.isNotEmpty() }
        
        print("Téléphone: ")
        val phone = readlnOrNull()?.trim()?.takeIf { it.isNotEmpty() }
        
        print("Site web: ")
        val website = readlnOrNull()?.trim()?.takeIf { it.isNotEmpty() }
        
        val contactInfo = ContactInfo(email, phone, website)
        
        if (!contactInfo.hasContact()) {
            println("❌ Au moins un moyen de contact (email ou téléphone) est requis")
            return
        }
        
        // Adresse optionnelle
        print("Ajouter une adresse? (o/N): ")
        val addAddress = readlnOrNull()?.trim()?.lowercase() == "o"
        
        var address: Address? = null
        if (addAddress) {
            print("Rue: ")
            val street = readlnOrNull()?.trim() ?: return
            
            print("Ville: ")
            val city = readlnOrNull()?.trim() ?: return
            
            print("Code postal: ")
            val postalCode = readlnOrNull()?.trim() ?: return
            
            print("Pays: ")
            val country = readlnOrNull()?.trim() ?: return
            
            address = Address(street, city, postalCode, country)
        }
        
        val result = supplierService.createSupplier(name, contactInfo, address)
        
        result.fold(
            onSuccess = { supplier ->
                println("✅ Fournisseur créé avec succès!")
                println("   ID: ${supplier.id.value}")
                println("   Nom: ${supplier.name}")
                println("   Email: ${supplier.contactInfo.email ?: "N/A"}")
                println("   Téléphone: ${supplier.contactInfo.phone ?: "N/A"}")
            },
            onFailure = { error ->
                println("❌ Erreur lors de la création: ${error.message}")
            }
        )
    }
    
    private suspend fun showSupplier(args: List<String>) {
        if (args.isEmpty()) {
            println("❌ ID du fournisseur manquant")
            return
        }
        
        val supplierId = SupplierId(args[0])
        val supplier = supplierService.getSupplier(supplierId)
        
        if (supplier == null) {
            println("❌ Fournisseur non trouvé: ${args[0]}")
            return
        }
        
        println("🏢 Détails du fournisseur:")
        println()
        println("ID: ${supplier.id.value}")
        println("Nom: ${supplier.name}")
        println("Email: ${supplier.contactInfo.email ?: "N/A"}")
        println("Téléphone: ${supplier.contactInfo.phone ?: "N/A"}")
        println("Site web: ${supplier.contactInfo.website ?: "N/A"}")
        println("Statut: ${if (supplier.isActive) "Actif" else "Inactif"}")
        
        supplier.address?.let { addr ->
            println("Adresse: $addr")
        }
        
        println("Créé le: ${supplier.createdAt}")
        println("Modifié le: ${supplier.updatedAt}")
    }
    
    private suspend fun searchSuppliers(args: List<String>) {
        if (args.isEmpty()) {
            println("❌ Terme de recherche manquant")
            return
        }
        
        val query = args.joinToString(" ")
        val suppliers = supplierService.searchSuppliers(query)
        
        if (suppliers.isEmpty()) {
            println("🔍 Aucun fournisseur trouvé pour: '$query'")
            return
        }
        
        println("🔍 Résultats de recherche pour '$query' (${suppliers.size}):")
        println()
        suppliers.forEach { supplier ->
            val status = if (supplier.isActive) "✅" else "❌"
            println("$status ${supplier.name} (${supplier.id.value})")
        }
    }
    
    override fun getHelp(): String = "Gestion des fournisseurs (list, create, show, search)"
}