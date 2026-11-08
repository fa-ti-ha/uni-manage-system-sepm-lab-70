# University Management (Spring Boot, MVC)

Roles: **ADMIN** (manages students, teachers, admins), **TEACHER** (own info), **STUDENT** (own info).
Website with login / logout (Spring Security, BCrypt passwords, role-based access) + a REST API.

Stack: Java 17 · Spring Boot 3.5 · Spring Web · Thymeleaf · Spring Data JPA · Spring Security ·
Bean Validation · PostgreSQL 17 · Docker Compose · Lombok · DevTools

## Updated UI
The website now uses a compact **Campus Desk** interface:
- Simple top navigation for Admin, Teacher and Student accounts.
- Cleaner login screen and smaller forms.
- Clear student/teacher/admin tables with compact action buttons.
- Responsive layout for laptop and mobile screens.

Only the Thymeleaf page templates and CSS were refreshed. Controllers, services, entities,
repositories, Spring Security rules, REST API endpoints and database configuration were left unchanged.
No additional frontend libraries are required.

## Run from IntelliJ
1. Start **Docker Desktop**.
2. Run `UniversityApplication`. Spring Boot starts the PostgreSQL container from `compose.yaml` by itself
   (Docker Compose Support), and Hibernate creates the tables.
3. Open http://localhost:8080 and log in.

| Role    | Username   | Password     |
|---------|------------|--------------|
| Admin   | `admin`    | `admin123`   |
| Teacher | `teacher1` | `teacher123` |
| Student | `student1` | `student123` |

## Run everything in Docker
```
docker compose --profile app up --build
```
Open http://localhost:8080.

## Website
| Page | Who |
|---|---|
| `/login`, Log out button | everyone |
| `/admin/students`, `/admin/teachers`, `/admin/admins` (list, add, edit, delete) | ADMIN |
| `/teacher/profile` (view + edit own info) | TEACHER |
| `/student/profile` (view + edit own info) | STUDENT |
| `/account/password` (change own password) | everyone logged in |

Opening a page of another role gives **403**.

## REST API (JSON, HTTP Basic login)
| Endpoint | Admin | Teacher | Student |
|---|---|---|---|
| `/api/admins` (GET, POST, PUT, DELETE) | all | - | - |
| `/api/teachers` | all | `GET /me`, read + update **own** record | - |
| `/api/students` | all | read all | `GET /me`, read + update **own** record |

Status codes: 200 OK, 201 Created, 204 No Content, 400 Bad Request, 401 Unauthorized,
403 Forbidden, 404 Not Found, 409 Conflict. Try the requests in `api-requests.http`.

## Tests (6 unit + 3 integration)
| Type | File | Tests |
|---|---|---|
| Unit (Mockito, no database) | `src/test/java/.../service/UserServiceTest.java` | 1. password is BCrypt-hashed and role set · 2. taken username → conflict · 3. wrong current password → conflict |
| Unit (Mockito, no database) | `src/test/java/.../service/StudentServiceTest.java` | 4. missing student → not found · 5. create saves student with STUDENT login · 6. update changes name and email |
| Integration (Spring + PostgreSQL) | `src/test/java/.../integration/StudentIntegrationTest.java` | 7. student saved in the database with a BCrypt password |
| Integration (Spring + PostgreSQL) | `src/test/java/.../integration/SecurityIntegrationTest.java` | 8. admin lists students via REST (200) · 9. student blocked from admin API (403) |

Run locally: start Docker Desktop, then right-click `src/test/java` → **Run 'All Tests'** (or `mvn test`).
On GitHub, `.github/workflows/ci.yml` runs all tests on every push with a PostgreSQL 17 service.

## Structure (MVC)
- `entity/` + `repository/` = Model (database)
- `service/` = business rules
- `controller/` = Controller (Thymeleaf pages) and `controller/api/` (REST, JSON)
- `form/` = data typed into the web forms
- `dto/` = JSON request/response objects (never contain the password)
- `resources/templates/` = View (Thymeleaf)
- `config/` = security, first users

## Troubleshooting
- **`Unable to start docker`**: start Docker Desktop (and restart IntelliJ after installing Docker).
- **Reset the database**: `docker compose down -v`
