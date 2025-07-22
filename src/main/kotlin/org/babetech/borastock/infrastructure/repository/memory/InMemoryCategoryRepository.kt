package org.babetech.borastock.infrastructure.repository.memory

import org.babetech.borastock.domain.model.Category
import org.babetech.borastock.domain.model.CategoryId
import org.babetech.borastock.domain.repository.CategoryRepository
import java.util.concurrent.ConcurrentHashMap

/**
 * Implémentation en mémoire du repository des catégories
 */
class InMemoryCategoryRepository : CategoryRepository {
    private val categories = ConcurrentHashMap<CategoryId, Category>()
    
    init {
        // Ajouter quelques catégories par défaut
        val defaultCategories = listOf(
            Category(CategoryId("CAT_001"), "Électronique", "Appareils électroniques"),
            Category(CategoryId("CAT_002"), "Informatique", "Matériel informatique"),
            Category(CategoryId("CAT_003"), "Accessoires", "Accessoires divers"),
            Category(CategoryId("CAT_004"), "Audio", "Équipements audio"),
            Category(CategoryId("CAT_005"), "Tablettes", "Tablettes et liseuses")
        )
        
        defaultCategories.forEach { category ->
            categories[category.id] = category
        }
    }
    
    override suspend fun findById(id: CategoryId): Category? = categories[id]
    
    override suspend fun findAll(): List<Category> = categories.values.toList()
    
    override suspend fun findByName(name: String): Category? = 
        categories.values.find { it.name.equals(name, ignoreCase = true) }
    
    override suspend fun save(category: Category): Category {
        categories[category.id] = category
        return category
    }
    
    override suspend fun delete(id: CategoryId): Boolean = 
        categories.remove(id) != null
    
    override suspend fun exists(id: CategoryId): Boolean = 
        categories.containsKey(id)
}