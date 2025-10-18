# Secure Transaction Logger (STL)

**STL** is a Spring Boot-based transaction logging service designed to provide **auditable and traceable transaction records**.  
It is intended to be deployed as a **Docker container** or in a **Kubernetes (k3s/k8s) cluster** and aims to follow standardized architectural and security practices.

---

## Overview

STL provides a centralized service for recording transactions with the following features:

- **Architectural Design** — The project is structured to follow layered design patterns (Controller → Service → Repository) and modular principles.  
- **Security Features** — Transactions are signed using HMAC or pluggable digital signatures (RSA/ECDSA), sensitive fields can be encrypted at rest using AES-GCM, and access is controlled via JWT-based authentication with role-based authorization.  
- **Auditability** — All API actions and transaction changes are recorded in an append-only audit log.  
- **Event Integration** — Transaction events can be published to message queues (Kafka or RabbitMQ) for further processing or monitoring.  
- **Observability** — Metrics, structured logs, and tracing are planned to allow monitoring and debugging of the system.  
- **Compliance-Oriented** — The design aims to align with security and operational standards for sensitive transaction processing.

---

## Architecture

STL is designed to separate concerns across layers and components:

- **Spring Boot Backend** — API endpoints, service layer for business rules, and persistence logic.  
- **PostgreSQL Database** — stores transaction and audit log records in an append-only fashion.  
- **Kafka / RabbitMQ** — optional event bus for transaction notifications.  
- **Vault (planned)** — external key management for encryption and signing keys.  
- **Prometheus + Grafana (planned)** — metrics collection and dashboarding.  
- **Jaeger / OpenTelemetry (planned)** — distributed tracing for event and request flows.

The project enforces **modular dependencies**, keeping controllers, services, repositories, and security components logically separated.

---

## Security and Integrity

STL aims to maintain transaction integrity and confidentiality using:

- **Digital Signatures** — HMAC or RSA/ECDSA for transaction authenticity.  
- **Hashing & Chaining** — previous transaction hashes are stored to detect tampering.  
- **Encryption** — sensitive fields are encrypted at rest (AES-GCM or KMS-managed keys).  
- **Role-Based Access Control** — endpoints are protected by roles (INGESTER, READER, AUDITOR, ADMIN).  
- **Audit Logging** — append-only logs record all modifications and system actions.

---

## Deployment

STL can be deployed in various environments:

- **Local Development**: Using Docker Compose with PostgreSQL and Kafka or RabbitMQ.  
- **CI/CD Pipeline**: Automated build, test, and Docker image generation using GitHub Actions, Jenkins, or GitLab CI.  
- **Cluster Deployment**: Helm charts or Kubernetes manifests for k3s/k8s clusters.

Example containers:
- `stl-backend` — Spring Boot API  
- `stl-db` — PostgreSQL  
- `stl-broker` — Kafka or RabbitMQ  
- `stl-monitor` — Prometheus + Grafana

---

## Testing Approach

Testing is integrated at multiple levels:

- **Unit Tests** — core business logic, cryptography, validation, and models.  
- **Integration Tests** — end-to-end testing with containerized databases and message queues.  
- **Architectural Tests** — ensures layer separation and module dependencies follow design constraints.  
- **Security Tests** — authentication, authorization, and static dependency analysis.

CI pipelines are set up to automatically run tests, build containers, and validate architectural constraints.

---

## Roadmap

STL is developed incrementally across 10 sprints, starting from project skeleton to production-ready hardening.  
See [`SPRINTS.md`](./SPRINTS.md) for a detailed sprint-by-sprint roadmap.

---

## Planned Enhancements

- External key management integration (Vault, AWS KMS, etc.).  
- Compliance auditing for standards like ISO 27001 or SOC 2.  
- Distributed tracing and observability improvements.  
- Extended event and audit log schemas for multi-system integrations.

---

## Target Use Cases

- Financial transaction logging and auditing.  
- IoT or telemetry event verification.  
- Enterprise transaction ingestion pipelines.  
- Audit and compliance platforms.

---

## License

TBD — MIT or Apache 2.0 recommended for open development.
