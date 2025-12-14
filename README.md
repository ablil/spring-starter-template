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

## Load testing (k6)

The k6 load test is run manually from the GitHub Actions workflow: `.github/workflows/k6.yml` (trigger: `workflow_dispatch`).

results:
- Each workflow run includes a **Markdown summary** in the run page (open the run → **Job Summary**).
- The **latest HTML report** is always published to [GitHub Pages](https://ablil.github.io/spring-starter-template/) (each run deploys the newest `loadtest/results/` output to `gh-pages`).


# Tips

Extract all environment variable defined in `application.yaml`

`grep -oh '\${[A-Z_][A-Z0-9_]*' app/**/application.yaml | sed 's/\${//'`