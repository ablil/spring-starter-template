package com.example.domain.todos

import java.io.Serializable
import java.time.Instant

data class Todo(
    val id: Long,
    val title: String,
    val description: String?,
    val dueTo: Instant?,
    val owner: String,
) : Serializable
