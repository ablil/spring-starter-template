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
abstract class Auditable(
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant,
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    var createdBy: String,
    @LastModifiedDate @Column(name = "updated_at", nullable = false) var updatedAt: Instant,
    @LastModifiedBy @Column(name = "updated_by", nullable = false) var updatedBy: String,
) {
    constructor() : this(Instant.now(), "unknown", Instant.now(), "unknown")
}

interface Identifiable<T> {
    var id: T
}
