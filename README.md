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

## Caching

By default, cache is disabled, however Redis is autoconfigured and can be run with **redis** spring profile.

*Use the provided docker container for local development*

## Performance tests

A separate module *perf-tests* has some dummy performance tests with Gatling. There are multiple way to run them:

**Locally**
```shell
./gradlew perf-tests:gatlingRung
```

**Jar file**

You can build jar file `./gradlew perf-tests:gatlingJar`, then run it elsewhere
```shell
java --add-opens java.base/java.lang=ALL-UNNAMED \
  -jar perf-tests/build/libs/gatling-performance-analysis.jar \
   -s todos.TodosSimulations -rf /tmp/gatling-results \
   -DbaseUrl=http://localhost:8080
```
**Docker image**

You can build a Docker image, and run it also elsewhere (e.g, cloud run jobs) `./gradlew perf-tests:jibDockerBuild`

```shell
docker run
```

**Github workflow**

A Github workflow is configured to be triggered manually, and it expects a base url to run against (e.g., http://myremotesystem.com)

By default, the latest run reports are uploaded to GitHub artifact and GitHub page, can be accessed [here](https://ablil.github.io/spring-starter-template/)

# Tips

Extract all environment variable defined in `application.yaml`

`grep -oh '\${[A-Z_][A-Z0-9_]*' app/**/application.yaml | sed 's/\${//'`