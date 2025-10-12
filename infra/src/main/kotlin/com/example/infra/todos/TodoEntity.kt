package com.example.infra.todos

import com.example.domain.todos.Todo
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "todos")
class TodoEntity(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) val id: Long,
    val title: String,
    val description: String?,
    val dueTo: Instant?,
    val owner: String,
) {

    constructor(
        todo: Todo
    ) : this(
        id = todo.id,
        title = todo.title,
        description = todo.description,
        dueTo = todo.dueTo,
        owner = todo.owner,
    )

    fun toTODO(): Todo =
        Todo(
            id = this.id,
            title = this.title,
            description = this.description ?: "",
            dueTo = this.dueTo,
            owner = this.owner,
        )
}
