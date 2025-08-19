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

## Debugging & Enhanced Logging

To simplify debugging and provide more detailed logs during development, a new `debug` Spring profile has been introduced.

You can activate this profile using the  `debug` target in the Makefile:

```bash
make debug
```

This command runs the application with the `SPRING_PROFILES_ACTIVE=debug` environment variable. When active, the following enhanced logging is enabled:

*   **Full HTTP Request & Response Logging:** Detailed logs including headers, payload, and query parameters for both incoming requests and outgoing responses.
*   **Spring Framework Debug Logs:** More verbose logging for Spring Web, Spring Security, and Spring Data JPA, providing deeper insights into framework operations.
*   **Application-Specific Debug Logs:** All logs from the `com.example` package will be shown at `DEBUG` level.

## Rate limiting

Rate limiting can be enabled by setting the property `example.rate-limiting.enabled` and a new security filter will be added to the security filter chain.

By default, requests are identified through their IP address from the `HttpServletRequest` or you can define a custom bean `RequestIdentifierResolver` to extract headers like *X-forwareded-for*.

By default, a sliding window rate limiting implementation is configured through `example.rate-limting.sliding-window` and `example.rate-limiting.max-rate`, however you can also define your own `RateLimiter` implementation.



# Tips

Extract all environment variable defined in `application.yaml`

`grep -oh '\${[A-Z_][A-Z0-9_]*' app/**/application.yaml | sed 's/\${//'`