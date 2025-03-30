package com.example.users

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import java.time.Instant
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Auditable<T> {
    abstract var id: T?

    @set:CreatedDate
    @set:Column(name = "created_at", nullable = false, updatable = false)
    abstract var createdAt: Instant

    @set:CreatedBy
    @set:Column(name = "created_by", nullable = false, updatable = false)
    abstract var createdBy: String

    @set:LastModifiedDate
    @set:Column(name = "updated_at", nullable = false)
    abstract var updatedAt: Instant

    @set:LastModifiedBy
    @set:Column(name = "updated_by", nullable = false)
    abstract var updatedBy: String
}
