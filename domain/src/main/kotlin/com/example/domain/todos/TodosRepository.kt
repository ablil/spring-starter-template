package com.example.domain.todos

interface TodosRepository {

    fun save(todo: Todo): Todo

    fun find(id: Long): Todo?

    fun exists(id: Long): Boolean

    fun delete(id: Long): Unit

    fun getAllByOwner(owner: String): Collection<Todo>
}
