package accounts

import io.gatling.javaapi.core.CoreDsl.RawFileBody
import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.http.HttpDsl.http

val login = http("sign in")
    .post("/api/v1/signin")
    .body(RawFileBody("signin.json"))
    .asJson()
    .check(
        jsonPath("$.token").saveAs("token")
    )

val signup = http("sign up")
    .post("/api/v1/signup")
    .body(RawFileBody("signup.json"))
    .asJson()
    .check(
        jsonPath("$.token").saveAs("token")
    )