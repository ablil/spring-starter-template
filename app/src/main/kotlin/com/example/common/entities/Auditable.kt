package com.example.common.entities

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import java.io.Serializable
import java.time.Instant
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

interface AuditableEntity {
    @set:CreatedBy
    @set:Column(name = "created_by", updatable = false, nullable = false)
    var createdBy: String

    @set:CreatedDate
    @set:Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: Instant

    @set:LastModifiedBy @set:Column(name = "updated_by", nullable = false) var updatedBy: String

    @set:LastModifiedDate @set:Column(name = "updated_at", nullable = false) var updatedAt: Instant
}

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity<T> : Identifiable<T>, AuditableEntity, Serializable {
    override lateinit var updatedBy: String
    override lateinit var updatedAt: Instant
    override lateinit var createdBy: String
    override lateinit var createdAt: Instant

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}

interface Identifiable<T> {
    var id: T
}
