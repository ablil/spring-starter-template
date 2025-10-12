package com.example.domain

import com.example.domain.todos.TodoService
import com.example.domain.todos.TodosRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TodosAutoConfiguration {

    @Bean fun todoService(repository: TodosRepository): TodoService = TodoService(repository)
}
