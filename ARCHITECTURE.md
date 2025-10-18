# STL — Architectural Package Responsibilities

This document describes the **responsibilities of each package** in the STL (Secure Transaction Logger) project and how they are expected to interact.  
It serves as a reference to maintain **layer separation**, guide development, and support architectural testing (unit and integration).

---

## Package Overview

### `controller`
**Responsibility:**  
- Expose REST API endpoints.  
- Receive client requests and delegate them to the appropriate services.  
- Perform basic input validation.  

**Architectural Rules:**  
- Must not contain business logic.  
- Must not access repositories or databases directly.  
- Must use services (`service/`) for any business operations.  

---

### `service`
**Responsibility:**  
- Implement **business logic**.  
- Orchestrate operations between repositories, utilities, and external services.  
- Ensure security rules and data consistency.  

**Architectural Rules:**  
- Must not expose persistence or infrastructure details.  
- Can use `repository/` and `util/`.  
- Can call other internal services only through defined interfaces.  

---

### `repository`
**Responsibility:**  
- Handle data persistence using JPA/Hibernate.  
- Implement specific database queries.  
- Maintain append-only persistence for transactions and audit logs.  

**Architectural Rules:**  
- Only interact with entities (`model/`) and the database.  
- Must not contain business logic.  

---

### `model`
**Responsibility:**  
- Define **entities, DTOs, and value objects**.  
- Serve as the data contract between layers.  

**Architectural Rules:**  
- Must not contain complex business logic.  
- Must not interact directly with the database or services.  

---

### `security`
**Responsibility:**  
- Configure global security (Spring Security).  
- Implement JWT filters, roles, and authorization checks.  
- Manage authentication and authorization mechanisms.  

**Architectural Rules:**  
- Used by controllers and services where needed.  
- Must not contain transaction business logic or persistence logic.  

---

### `config`
**Responsibility:**  
- Provide **centralized Spring Boot configuration** (beans, profiles, Kafka/PostgreSQL integration).  
- Contain infrastructure configuration components.  

**Architectural Rules:**  
- Must not contain business logic or access repositories directly.  
- Should remain independent of `service` and `controller`.  

---

### `util`
**Responsibility:**  
- Provide **reusable helper functions** (hashing, signatures, integrity checks, transaction chaining).  
- Maintain logic independent of business layers.  

**Architectural Rules:**  
- Must not access the database or expose endpoints.  
- Can be used by `service` and `security` layers for common operations.  

---

### General Principles
- Each package must respect **separation of concerns**.  
- Dependencies should **only flow downward** (Controller → Service → Repository → Model).  
- Violations of these rules should be caught using **architectural tests** (e.g., ArchUnit).  
- Any addition of packages or changes in responsibilities must be documented here.  

---

This file should be **updated whenever a new package is added or responsibilities change**, serving as a living guide for the project.

