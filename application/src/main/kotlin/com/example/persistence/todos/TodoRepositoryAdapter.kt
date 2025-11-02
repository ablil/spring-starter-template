package com.example.persistence.todos

import com.example.domain.todos.Todo
import com.example.domain.todos.TodosRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
interface TodoRepositoryAdapter : TodosRepository, JpaRepository<TodoEntity, Long> {

    @CachePut(cacheNames = ["cache1"], key = "#result.id")
    override fun save(todo: Todo): Todo = this.save<TodoEntity>(TodoEntity(todo)).toTODO()

    @Cacheable("cache1") override fun find(id: Long): Todo? = this.findByIdOrNull(id)?.toTODO()

    override fun exists(id: Long): Boolean = this.existsById(id)

    @CacheEvict("cache1")
    override fun delete(id: Long) {
        this.deleteById(id)
    }

    override fun getAllByOwner(owner: String): Collection<Todo> =
        this.findAllByCreatedBy(owner).map { it.toTODO() }

    fun findAllByCreatedBy(username: String): Collection<TodoEntity>
}
