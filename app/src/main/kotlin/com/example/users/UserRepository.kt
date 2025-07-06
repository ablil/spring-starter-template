package com.example.users

import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.transaction.annotation.Transactional

const val DEFAULT_CACHE = "default"

interface UserRepository :
    JpaRepository<DomainUser, Long>, PagingAndSortingRepository<DomainUser, Long> {

    @Cacheable(cacheNames = [DEFAULT_CACHE], key = "#username", condition = "#username == #email")
    fun findByUsernameOrEmailIgnoreCase(username: String, email: String): DomainUser?

    fun findByUsernameIgnoreCase(username: String): DomainUser?

    fun findByEmailIgnoreCase(email: String): DomainUser?

    fun existsByEmailIgnoreCase(email: String): Boolean

    fun findOneByActivationKey(key: String): DomainUser?

    fun findOneByResetKey(key: String): DomainUser?

    @Transactional fun deleteByUsernameIgnoreCase(username: String): Int
}
