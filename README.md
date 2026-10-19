# University Management System

A Spring Boot web app demonstrating **REST API design**, **Spring Security** (role-based access), and **Docker containerization**. Manages three resources: admins, teachers, and students.

## Stack

| Layer     | Technology                                  |
| --------- | ------------------------------------------- |
| Backend   | Java 17, Spring Boot (Web, Security, JPA)   |
| UI        | Thymeleaf + plain HTML/CSS                  |
| Database  | MySQL 8.4 (Docker) or XAMPP (local dev)     |
| Container | Docker + Docker Compose                     |

---

## Run with Docker (recommended)

Requires: Docker Desktop.

```bash
docker compose up --build
```

Then open **http://localhost:8080**.

That single command builds the app image and starts two containers:

- **`app`** — your Spring Boot app, port `8080` published to your machine
- **`db`** — MySQL 8.4, only reachable *from other containers*, not from your laptop

The database is created automatically, the schema is generated on startup, and the demo accounts are seeded on first run.

### Stopping

```bash
docker compose stop      # stop (data kept)
docker compose start     # start again

docker compose down      # remove containers + network (data kept)
docker compose down -v   # remove containers AND delete all data  ← careful
```

The database lives in a named volume (`db-data`), so `docker compose down` does **not** delete records. Only `-v` does.

> **If port 8080 is already in use**, change the mapping in `compose.yaml`:
> ```yaml
> ports:
>   - "8081:8080"    # host:container
> ```
> and visit `http://localhost:8081` instead.

---

## Run locally (without Docker)

For day-to-day development.

1. Start **XAMPP** → turn on **MySQL** (port `3306`).
2. From the project folder:

```bash
./mvnw.cmd spring-boot:run
```

`application.properties` points at `localhost:3306`, user `root`, empty password — XAMPP defaults. The database and schema are created automatically.

---

## Demo accounts

| Role    | Username  | Password     |
| ------- | --------- | ------------ |
| ADMIN   | `admin`   | `admin123`   |
| TEACHER | `teacher` | `teacher123` |
| STUDENT | `student` | `student123` |

Passwords are BCrypt-hashed. You are redirected to your role's dashboard after login.

## Permissions

| Role    | Admins | Teachers | Students |
| ------- | :----: | :------: | :------: |
| ADMIN   | ✅     | ✅       | ✅       |
| TEACHER | ❌     | ✅       | ❌       |
| STUDENT | ❌     | ❌       | ✅       |

Violations return **403** as JSON. Unauthenticated API calls return **401** as JSON.

---

## REST API

All endpoints return **JSON**. Resource names are plural and lowercase.

| Method   | Endpoint               | Meaning            | Success |
| -------- | ---------------------- | ------------------ | ------- |
| `GET`    | `/api/admins`          | list all           | 200     |
| `GET`    | `/api/admins/{id}`     | get one            | 200     |
| `POST`   | `/api/admins`          | create             | 201     |
| `PUT`    | `/api/admins/{id}`     | update             | 200     |
| `DELETE` | `/api/admins/{id}`     | delete             | 204     |

Same pattern for `/api/teachers` and `/api/students`.

Errors are JSON too: **400** validation, **401** not authenticated, **403** forbidden (including CSRF), **404** not found.

### Trying it out

The UI's list and form pages call these endpoints with `fetch()`, so the browser is the easiest way to exercise them. For manual calls, note that write requests need:

1. **Authentication** — log in first to get a session.
2. **CSRF token** — send it in the `X-CSRF-TOKEN` header (it rotates on login).

```
GET  /api/students        → 200  [ ... ]
POST /api/students        → 201  { ... }
DELETE /api/students/3    → 204  (no body)
DELETE /api/students/1    → 403  { "error": "You cannot delete your own account" }
```

---

## Project layout

```
src/main/java/com/softeng/ums/
├── entity/          Admin, Teacher, Student
├── repository/      JPA repositories
├── security/        UserDetailsService, SecurityConfig, JSON error handlers
├── controller/
│   ├── *Controller.java      REST CRUD (JSON) for each resource
│   ├── PageController.java   page shells (render templates)
│   ├── HomeController.java   dashboard
│   └── LoginController.java  login page
└── errors/          ApiError + exception handler

src/main/resources/templates/   login, home, list, form (shared by all 3)
Dockerfile                      multi-stage: Maven build → JRE runtime
compose.yaml                    app + MySQL + named volume + healthcheck
```
