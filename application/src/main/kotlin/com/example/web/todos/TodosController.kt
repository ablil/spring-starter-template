package com.example.web.todos

import com.example.domain.todos.Todo
import com.example.domain.todos.TodoService
import com.example.security.SecurityUtils
import java.time.Instant
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/todos")
class TodosController(val service: TodoService) {

    @GetMapping
    fun getAllTodos(): Collection<Todo> =
        service.getUserTodos(SecurityUtils.authenticatedPrincipal())

    @PostMapping
    fun createTodo(@RequestBody dto: TodoRequest): Todo =
        service.create(
            Todo(
                id = 0,
                title = dto.title,
                description = dto.description,
                dueTo = dto.dueTo,
                owner = SecurityUtils.authenticatedPrincipal(),
            )
        )

    @PutMapping("/{id}")
    fun edit(@PathVariable("id") id: Long, @RequestBody dto: TodoRequest): Todo =
        service.edit(
            Todo(id, dto.title, dto.description, dto.dueTo, SecurityUtils.authenticatedPrincipal())
        )

    @DeleteMapping("/{id}") fun deleteTodo(@PathVariable id: Long): Unit = service.delete(id)
}

data class TodoRequest(val title: String, val description: String?, val dueTo: Instant?)
