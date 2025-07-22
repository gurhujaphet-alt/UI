package org.babetech.borastock.infrastructure.repository.memory

import org.babetech.borastock.domain.model.*
import org.babetech.borastock.domain.repository.UserRepository
import java.util.concurrent.ConcurrentHashMap

/**
 * Implémentation en mémoire du repository des utilisateurs
 */
class InMemoryUserRepository : UserRepository {
    private val users = ConcurrentHashMap<UserId, User>()
    
    init {
        // Ajouter un utilisateur admin par défaut
        val now = kotlinx.datetime.Clock.System.now()
        val adminUser = User(
            id = UserId("USR_ADMIN"),
            email = "admin@borastock.com",
            name = "Administrateur",
            role = UserRole.ADMIN,
            createdAt = now
        )
        users[adminUser.id] = adminUser
    }
    
    override suspend fun findById(id: UserId): User? = users[id]
    
    override suspend fun findByEmail(email: String): User? = 
        users.values.find { it.email.equals(email, ignoreCase = true) }
    
    override suspend fun findAll(): List<User> = users.values.toList()
    
    override suspend fun findByRole(role: UserRole): List<User> = 
        users.values.filter { it.role == role }
    
    override suspend fun findActive(): List<User> = 
        users.values.filter { it.isActive }
    
    override suspend fun save(user: User): User {
        users[user.id] = user
        return user
    }
    
    override suspend fun delete(id: UserId): Boolean = 
        users.remove(id) != null
    
    override suspend fun exists(id: UserId): Boolean = 
        users.containsKey(id)
    
    override suspend fun existsByEmail(email: String): Boolean = 
        users.values.any { it.email.equals(email, ignoreCase = true) }
    
    override suspend fun count(): Long = users.size.toLong()
    
    override suspend fun countByRole(role: UserRole): Long = 
        users.values.count { it.role == role }.toLong()
}