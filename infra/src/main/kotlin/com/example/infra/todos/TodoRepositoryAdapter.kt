package com.example.infra.todos

import com.example.domain.todos.Todo
import com.example.domain.todos.TodosRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
interface TodoRepositoryAdapter : TodosRepository, JpaRepository<TodoEntity, Long> {

    override fun save(todo: Todo): Todo = this.save<TodoEntity>(TodoEntity(todo)).toTODO()

    override fun find(id: Long): Todo? = this.findByIdOrNull(id)?.toTODO()

    override fun exists(id: Long): Boolean = this.existsById(id)

    override fun delete(id: Long) {
        this.deleteById(id)
    }

    override fun getAllByOwner(owner: String): Collection<Todo> =
        this.findAllByOwner(owner).map { it.toTODO() }

    fun findAllByOwner(owner: String): Collection<TodoEntity>
}
