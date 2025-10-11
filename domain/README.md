# Domain module

In Domain Driven developement (DDD), this is the heart of your system, where all the business logic,
rules, and core concepts live. Unlike the infra module, it must remain framework-agnostic.

> **This module ideally should be pure kotlin and requires no external dependency**

Purpose:

1. Encapsulate the business rules
   * All rules, validations, and invariants live here.
   * The domain is the source of truth for business behavior.
2. Define domain models (entities, value objects, aggregates)
   * Entities: objects with identity (User, Order, Invoice)
   * Value Objects: immutable objects with no identity, defined by their attributes (Email, Money, Address)
   * Aggregates: clusters of entities/value objects treated as a unit (Order with OrderLines)
3. Define ports/interfaces
   * Expose contracts that must be implemented elsewhere (usually in infra or external adapters):
   * Repositories (UserRepositoryPort)
   * External services (EmailSenderPort, PaymentGatewayPort)
   * Domain depends on nothing outside itself, only defines interfaces.
4. Provide domain services
   * Stateless or stateful operations that orchestrate domain logic.
   * Examples:
   * UserRegistrationService (uses repository port and business rules)
   * PaymentProcessingService
   * Services focus purely on business behavior, not on database or HTTP.
5. Optional: domain events
   * Events describing changes in the domain:
   * UserRegisteredEvent
   * OrderShippedEvent
   * These are pure objects, consumers of events are handled outside (infra / application layer).

