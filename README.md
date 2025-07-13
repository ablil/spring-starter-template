# Spring starter template

[![CI](https://github.com/ablil/spring-starter-template/actions/workflows/ci.yaml/badge.svg)](https://github.com/ablil/spring-starter-template/actions/workflows/ci.yaml)

Use this template as a baseline for spinning up rest API with Spring boot / Kotlin.

# Get started

## Run locally
After create your project from this template, you can run the application by following these steps:
1. start Postgres container: `docker compose up -d db`
2. start the app: `./gradlew app:bootRun`
3. check health: `curl http://localhost:8080/actuator/health`

## API First approach
This project adopt API first approach to define new APIs, and generate code via 
[openapi-generator](https://github.com/OpenAPITools/openapi-generator).

1. Define your API in *api/**/resources/static/oas3*
2. Generate code `./gradlew api:openApiGenerate`
3. Implement API
4. Expose API through [swagger-ui](https://springdoc.org/#swagger-ui-properties)
(*check springdoc properties on application.yaml*)

## Observability & monitoring

[Prometheus](https://prometheus.io/) is an open source system that collect and store metrics from
your applications, which will be visualized in [Grafana]().

Some metrics are enabled by default (eg, Hibernate, cache), and some Docker images are provided also.
