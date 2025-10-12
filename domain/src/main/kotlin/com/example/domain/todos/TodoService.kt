package com.example.domain.todos

class TodoService(val repository: TodosRepository) {

    fun create(todo: Todo): Todo = repository.save(todo)

    fun edit(todo: Todo): Todo =
        if (!repository.exists(todo.id)) {
            throw TodoNotFound("todo ${todo.id} does NOT exists")
        } else {
            repository.save(todo)
        }

    fun delete(id: Long): Unit = repository.delete(id)

    fun getUserTodos(owner: String): Collection<Todo> = repository.getAllByOwner(owner)
}

class TodoNotFound(val msg: String) : RuntimeException(msg)
