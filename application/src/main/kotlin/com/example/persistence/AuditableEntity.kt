package com.example.persistence

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
abstract class AuditableEntity(
    @set:CreatedBy var createdBy: String? = null,
    @set:LastModifiedBy var lastModifiedBy: String? = null,
    @set:CreatedDate var createdAt: Instant = Instant.MIN,
    @set:LastModifiedDate var lastModifiedAt: Instant = Instant.MIN,
)
