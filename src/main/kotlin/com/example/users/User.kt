package com.example.users

import com.example.common.AuthorityConstants
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

@Entity
@Table(name = "users")
data class User(
    @Column(unique = true) val username: String,
    @Column(unique = true) val email: String,
    val password: String,
    val disabled: Boolean,
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    val roles: Set<AuthorityConstants>,
    val firstName: String?,
    val lastName: String?,
    val activationKey: String?,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) override var id: Long? = null,
    override var createdAt: Instant = Instant.now(),
    override var updatedAt: Instant = Instant.now(),
    override var createdBy: String = "n/a",
    override var updatedBy: String = "n/a",
) : Auditable<Long>() {

    val fullName: String?
        get() = "%s %s".format(firstName, lastName)

    companion object
}
