# Infra module

In Domain-Driven Design (DDD), the infrastructure (“infra”) module has a very specific role:
it acts as a technical adapter layer, connecting your domain logic to external systems,
frameworks, or resources, without polluting the domain with technical concerns.

*Think of it like the “glue” between your pure domain logic and the outside world.*

Purpose:

1. Provide implementations of domain ports/interfaces
   * Domain defines interfaces (ports) for repositories, messaging, or external services.
   * Infra implements these interfaces using actual technologies:
   * JPA repositories
   * Kafka publishers/subscribers
   * REST or gRPC clients
   * File storage adapters
   * Payment gateways, email senders, etc.
2. Map between domain models and technical representations
   * Often domain objects are pure Kotlin/Java classes, independent of frameworks.
   * Infra handles mapping to persistence entities, DTOs, or external schemas.
   * Example: User domain object ↔ UserEntity JPA entity.
3. Provide technical configuration
   * Data source configuration, connection pools, transaction managers, messaging clients.
   * Spring beans like @Repository, @Component, @Bean are usually declared here if they are tied to technical resources.
   * Optional: can include technical helpers for logging, caching, etc.
4. Encapsulate framework-specific code
   * Infra knows about Spring, Hibernate, JDBC, Kafka, etc.
   * Domain knows nothing about frameworks.