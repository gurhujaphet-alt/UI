package org.babetech.borastock.domain.repository

import org.babetech.borastock.domain.model.User
import org.babetech.borastock.domain.model.UserId
import org.babetech.borastock.domain.model.UserRole

/**
 * Interface du repository pour les utilisateurs
 */
interface UserRepository {
    suspend fun findById(id: UserId): User?
    suspend fun findByEmail(email: String): User?
    suspend fun findAll(): List<User>
    suspend fun findByRole(role: UserRole): List<User>
    suspend fun findActive(): List<User>
    
    suspend fun save(user: User): User
    suspend fun delete(id: UserId): Boolean
    suspend fun exists(id: UserId): Boolean
    suspend fun existsByEmail(email: String): Boolean
    
    suspend fun count(): Long
    suspend fun countByRole(role: UserRole): Long
}