package com.example.users

import com.example.common.persistence.BaseEntity
import com.example.common.security.AuthorityConstants
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

enum class UserStatus {
    ACTIVE,
    INACTIVE,
    WAITING_FOR_CONFIRMATION,
}

@Entity
@Table(name = "domain_users")
@Suppress("LongParameterList")
class DomainUser(
    @Column(unique = true) var username: String,
    @Column(unique = true) var email: String,
    @JsonIgnore var password: String,
    @Enumerated(EnumType.STRING) var status: UserStatus = UserStatus.INACTIVE,
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = AuthorityConstants::class)
    @CollectionTable(name = "authorities")
    var roles: Set<AuthorityConstants> = emptySet(),
    var firstName: String? = null,
    var lastName: String? = null,
    @JsonIgnore var activationKey: String? = null,
    @JsonIgnore var resetKey: String? = null,
    @JsonIgnore var resetDate: Instant? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) override var id: Long = 0,
) : BaseEntity<Long>() {

    fun disableAccount(key: String) {
        this.activationKey = key
        this.status = UserStatus.INACTIVE
    }

    fun activateAccount() {
        status = UserStatus.ACTIVE
        activationKey = null
        resetKey = null
        resetDate = null
    }

    fun resetAccount(key: String) {
        resetKey = key
        resetDate = Instant.now()
        this.status = UserStatus.WAITING_FOR_CONFIRMATION
    }

    fun updatePassword(encodedPassword: String) {
        check(encodedPassword != this.password) { "tried using the same password" }
        password = encodedPassword
        resetKey = null
        resetDate = null
    }

    fun updateUserInfo(
        email: String,
        firstName: String?,
        lastName: String?,
        roles: Set<AuthorityConstants>?,
    ) {
        this.email = email
        this.firstName = firstName
        this.lastName = lastName
        this.roles = roles ?: emptySet()
    }

    val fullName: String?
        get() = listOfNotNull(lastName, firstName).takeUnless { it.isEmpty() }?.joinToString(" ")

    fun isActive(): Boolean = UserStatus.ACTIVE == this.status

    companion object {

        fun newUser(
            username: String,
            email: String,
            password: String,
            firstName: String? = null,
            lastName: String? = null,
            roles: Set<AuthorityConstants>? = emptySet(),
        ): DomainUser =
            DomainUser(
                username = username,
                email = email,
                password = password,
                status = UserStatus.INACTIVE,
                roles = roles ?: emptySet(),
                firstName = firstName,
                lastName = lastName,
                activationKey = generateRandomKey(),
                resetKey = null,
                resetDate = null,
                id = 0,
            )
    }
}
