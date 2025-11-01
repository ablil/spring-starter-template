package todos

import io.gatling.javaapi.core.CoreDsl.RawFileBody
import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.http.HttpDsl.http

// actions
val createTodo = http("create todo")
    .post("/api/v1/todos")
    .header("Authorization", "Bearer #{token}")
    .body(RawFileBody("body.json"))
    .asJson()
    .check(
        jsonPath("$.id").saveAs("todoId")
    )

val editTodo = http("edit todo")
    .put("/api/v1/todos/#{todoId}")
    .header("Authorization", "Bearer #{token}")
    .body(RawFileBody("body.json"))
    .asJson()

val listTodos = http("list todos")
    .get("/api/v1/todos")
    .header("Authorization", "Bearer #{token}")
    .asJson()


val deleteTodo = http("delete todo")
    .delete("/api/v1/todos/#{todoId}")
    .header("Authorization", "Bearer #{token}")
