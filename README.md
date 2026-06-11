# Reference Data Aggregation Service (RDAS)

A high-performance Spring Boot microservice that transforms a slow, fragmented SOAP-based country information provider into a modern, searchable, paginated, and resilient REST API.

RDAS aggregates reference data from `webservices.oorsprong.org`, normalizes it into a unified domain model, and serves requests through a multi-layer caching architecture designed for low latency, fault tolerance, and operational reliability.

---

#  Key Features

* **SOAP-to-REST Abstraction Layer**

  * Converts multiple SOAP operations into a single REST-friendly data model.

* **Asynchronous Cache Warm-Up**

  * Loads and aggregates country data in the background immediately after application startup.
  * API becomes available instantly without waiting for expensive upstream requests.

* **Redis-Powered Distributed Cache**

  * Primary storage layer for country reference data.
  * 24-hour TTL with automated nightly refresh.

* **In-Memory Standby Cache**

  * Provides sub-millisecond fallback responses if Redis becomes unavailable.

* **Resilience4j Circuit Breaker**

  * Protects the application from upstream SOAP outages.
  * Automatically serves cached snapshots during external service failures.

* **Thread-Safe Cache Synchronization**

  * Prevents duplicate aggregation jobs through synchronized cache refresh logic.

* **Pagination, Search & Sorting**

  * Supports efficient filtering and navigation of large datasets.

* **Cloud-Native Deployment**

  * Dockerized runtime.
  * Kubernetes-ready manifests.
  * Health monitoring via Spring Actuator.

---

# 🏗️ Architecture Overview

```text
                    ┌─────────────────────┐
                    │  SOAP Data Provider │
                    │ oorsprong.org SOAP  │
                    └──────────┬──────────┘
                               │
                               ▼
                  ┌─────────────────────────┐
                  │ SOAP Aggregation Engine │
                  └──────────┬──────────────┘
                             │
                  Background Cache Warmup
                             │
                             ▼
                 ┌─────────────────────────┐
                 │        Redis Cache      │
                 │ Primary Data Store      │
                 └──────────┬──────────────┘
                            │
                            ▼
             ┌──────────────────────────────┐
             │ In-Memory Backup Snapshot    │
             │ Secondary Failover Layer     │
             └──────────────┬───────────────┘
                            │
                            ▼
                ┌─────────────────────────┐
                │      REST API Layer     │
                └──────────┬──────────────┘
                           │
                           ▼
                      API Clients
```

---

#  Performance Strategy

Fetching country metadata directly from the SOAP provider requires multiple sequential requests per country.

With over 240 countries available, cold retrieval can exceed **2 minutes**.

RDAS eliminates this bottleneck through:

1. Startup background aggregation
2. Redis snapshot storage
3. In-memory failover copies
4. Scheduled refresh operations
5. Circuit breaker protection

As a result, API consumers receive responses from cache instead of repeatedly hitting the external SOAP service.

---

#  Technology Stack

| Layer            | Technology          |
| ---------------- | ------------------- |
| Language         | Java 17             |
| Framework        | Spring Boot 3       |
| SOAP Integration | Spring Web Services |
| Cache            | Redis               |
| Resilience       | Resilience4j        |
| Scheduling       | Spring Scheduler    |
| Containerization | Docker              |
| Orchestration    | Kubernetes          |
| Monitoring       | Spring Actuator     |
| Build Tool       | Maven               |

---

#  Project Structure

```text
src
├── controller
│   └── CountryController
├── service
│   └── CountryService
├── soap
│   └── SoapClient
├── model
│   └── CountryDetails
├── config
├── exception
└── RdasApplication
```

---

#  Running Locally

## Prerequisites

* Java 17+
* Docker
* Docker Compose
* Maven 3.9+

---

## Option 1: Docker Compose (Recommended)

Build and start the complete stack:

```bash
docker compose up --build -d
```

View logs:

```bash
docker compose logs -f rdas-app
```

Stop and remove resources:

```bash
docker compose down -v
```

---

# 🐳 Docker Services

The compose stack includes:

| Service    | Purpose         |
| ---------- | --------------- |
| rdas-app   | Spring Boot API |
| rdas-redis | Redis Cache     |

Application endpoint:

```text
http://localhost:8080
```

---

# ☸️ Kubernetes Deployment

Build local image:

```bash
docker build -t rdas-app:latest .
```

Deploy Redis:

```bash
kubectl apply -f k8s-redis.yml
```

Deploy Application:

```bash
kubectl apply -f k8s-app.yml
```

Monitor resources:

```bash
kubectl get pods -w
```

Retrieve service URL:

```bash
minikube service rdas-app-service --url
```

---

#  Cache Lifecycle

### Startup

```text
Application Starts
       │
       ▼
Background Warmup Triggered
       │
       ▼
SOAP Aggregation
       │
       ▼
Redis Updated
       │
       ▼
Memory Backup Updated
```

### Scheduled Refresh

Runs daily at:

```text
02:00 AM
```

Cron Expression:

```java
@Scheduled(cron = "0 0 2 * * *")
```

---

#  Resilience Strategy

### Normal Operation

```text
Client
  │
  ▼
Redis Cache
  │
  ▼
Response
```

### Redis Failure

```text
Client
  │
  ▼
Memory Backup
  │
  ▼
Response
```

### SOAP Outage

```text
Circuit Breaker OPEN
        │
        ▼
Redis Snapshot
        │
        ▼
Memory Backup
        │
        ▼
Response
```

This ensures uninterrupted service availability even during prolonged upstream outages.

---

# API Endpoints

## Get All Countries

```http
GET /api/v1/countries
```

Example:

```bash
curl http://localhost:8080/api/v1/countries
```

---

## Search Countries

```http
GET /api/v1/countries?search=Kenya
```

Example:

```bash
curl http://localhost:8080/api/v1/countries?search=Kenya
```

---

## Pagination

```http
GET /api/v1/countries?page=0&size=20
```

---

## Sorting

```http
GET /api/v1/countries?sort=continent,desc
```

---

## Country By ISO Code

```http
GET /api/v1/countries/KE
```

---

# 🔍 Health Monitoring

Spring Actuator endpoints:

```http
GET /actuator/health
GET /actuator/info
```

Example:

```bash
curl http://localhost:8080/actuator/health
```

---

# ⚙️ Configuration

Environment Variables:

```properties
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
```

Default values are automatically applied when variables are not supplied.

---

#  Production Considerations

Recommended enhancements for production deployments:

* Redis persistence backups
* Horizontal pod autoscaling
* Distributed locking for cache refresh jobs
* OpenTelemetry tracing
* Kubernetes Secrets management

---

# 🧪 Verification

Basic API validation:

```bash
curl http://localhost:8080/api/v1/countries
curl http://localhost:8080/api/v1/countries?search=KE
curl http://localhost:8080/api/v1/countries?page=2&size=5
curl http://localhost:8080/api/v1/countries?sort=continent,desc
curl http://localhost:8080/api/v1/countries/KE
```

---
