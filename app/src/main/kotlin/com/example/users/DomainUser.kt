package com.example.users

import com.example.common.persistence.Auditable
import com.example.common.persistence.Identifiable
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
import org.springframework.data.annotation.PersistenceCreator

@Entity
@Table(name = "domain_users")
@Suppress("LongParameterList")
class DomainUser
@PersistenceCreator
constructor(
    @Column(unique = true) var username: String,
    @Column(unique = true) var email: String,
    @JsonIgnore var password: String,
    var disabled: Boolean = true,
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
) : Auditable(), Identifiable<Long> {

    fun activate() {
        disabled = false
        activationKey = null
    }

    fun initPasswordReset(key: String) {
        resetKey = key
        resetDate = Instant.now()
    }

    fun finishResetPassword(encodedPassword: String) {
        password = encodedPassword
        resetKey = null
        resetDate = null
    }

    val fullName: String?
        get() = "%s %s".format(firstName, lastName)

    companion object
}
