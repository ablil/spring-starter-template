# Spring starter template

Use this template as a baseline for spinning up rest API with Spring boot / Kotlin.

## Features

By default, this template includes (not an exhaustive list though):
* login with username/email and password
* registration and password reset
* admin users management

*Open swagger-ui to display all available endpoints*

## Get started
After create your project from this template, you can run the application by following these steps:
1. start Postgres container: `docker compose up -d`
2. start the app: `./gradlew bootRun`
3. check health: `curl http://localhost:8080/actuator/health`
