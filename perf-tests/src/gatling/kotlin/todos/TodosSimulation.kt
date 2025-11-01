package todos

import accounts.login
import io.gatling.javaapi.core.CoreDsl.constantUsersPerSec
import io.gatling.javaapi.core.CoreDsl.global
import io.gatling.javaapi.core.CoreDsl.pause
import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import java.time.Duration

class TodosSimulation : Simulation() {

    val httpProtocol = http.baseUrl(System.getProperty("baseUrl", "http://localhost:8080"))
        .acceptHeader("application/json")
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")


    val myscenario = scenario("Todo").exec(
        login,
        pause(1),
        createTodo,
        pause(1),
        editTodo,
        pause(1),
        listTodos,
        pause(1),
        deleteTodo
    )

    init {
        setUp(
            myscenario.injectOpen(
                constantUsersPerSec(2.0).during(Duration.ofSeconds(5))
            )
        )
            .assertions(
                global().responseTime().max().lt(1000),
                global().successfulRequests().percent().`is`(100.0)
            )
            .protocols(httpProtocol)
    }
}