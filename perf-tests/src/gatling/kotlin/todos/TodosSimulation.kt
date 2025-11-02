package todos

import accounts.login
import io.gatling.javaapi.core.CoreDsl.constantUsersPerSec
import io.gatling.javaapi.core.CoreDsl.global
import io.gatling.javaapi.core.CoreDsl.incrementUsersPerSec
import io.gatling.javaapi.core.CoreDsl.pause
import io.gatling.javaapi.core.CoreDsl.rampUsers
import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.CoreDsl.stressPeakUsers
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import java.lang.IllegalArgumentException
import java.time.Duration

class TodosSimulation : Simulation() {

    val httpProtocol = http.baseUrl(System.getProperty("baseUrl", "http://localhost:8080"))
        .acceptHeader("application/json")
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")


    val myscenario = scenario("Todo")
        .exec(login)
        .exitHereIfFailed()
        .exec(
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
        val type = System.getProperty("type", "soak")
        val users = System.getProperty("users", "10")
        val seconds = System.getProperty("seconds", "10")
        setUp(
            myscenario.injectOpen(
                when (type) {
                    "incrementPerSec" -> incrementUsersPerSec(users.toDouble()).times(3)
                        .eachLevelLasting(Duration.ofSeconds(seconds.toLong()))

                    "stressPeak" -> stressPeakUsers(users.toInt()).during(Duration.ofSeconds(seconds.toLong()))
                    "constantPerSec" -> constantUsersPerSec(users.toDouble()).during(Duration.ofSeconds(seconds.toLong()))
                    "ramp" -> rampUsers(users.toInt()).during(Duration.ofSeconds(seconds.toLong()))
                    else -> throw IllegalArgumentException("invalid test type")
                }
            )
        )
            .assertions(
                global().responseTime().max().lt(1000),
                global().successfulRequests().percent().gte(95.0)
            )
            .protocols(httpProtocol)
    }
}