package com.example.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface UserRepository :
    JpaRepository<DomainUser, Long>, PagingAndSortingRepository<DomainUser, Long> {

    fun existsByUsernameOrEmailIgnoreCase(username: String, email: String): Boolean

    fun findByUsernameOrEmailIgnoreCase(username: String, email: String): DomainUser?

    fun findByUsernameIgnoreCase(username: String): DomainUser?

    fun findByEmailIgnoreCase(email: String): DomainUser?

    fun existsByEmailIgnoreCase(email: String): Boolean

    fun findOneByActivationKey(key: String): DomainUser?

    fun findOneByResetKey(key: String): DomainUser?
}
