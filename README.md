# HR Management System

Enterprise-grade Employee Management System built with **Spring Boot 3** + **React 18**.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2, Java 17, Spring Security (JWT), Spring Data JPA |
| Database | MySQL 8.x |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Frontend | React 18, Vite, TailwindCSS, TanStack Query |
| Proxy | Nginx |

---

## Project Structure

```
├── backend/                  # Spring Boot application
│   ├── pom.xml
│   └── src/main/java/com/enterprise/hr/
│       ├── HrApplication.java
│       ├── config/           # Security, CORS, OpenAPI, DataInitializer
│       ├── controller/       # AuthController, UserController, TaskController, RoleController
│       ├── service/          # AuthService, UserService, TaskService, EmailService
│       ├── repository/       # JPA repositories
│       ├── model/            # User, Role, Task entities + enums
│       ├── dto/              # Request/Response DTOs
│       └── security/         # JWT filter, provider, SecurityUtils
├── frontend/                 # React + Vite application
│   ├── src/
│   │   ├── pages/            # LoginPage, RegisterPage, VerifyEmailPage, Dashboard, ...
│   │   ├── components/       # Layout, shared components
│   │   ├── services/         # API client (axios)
│   │   └── store/            # Zustand auth store
│   └── vite.config.js
└── nginx/
    └── nginx.conf            # Reverse proxy config
```

---

## Features

### User Management
- Email registration with SMTP verification code
- JWT-based authentication (access + refresh tokens)
- Soft-delete users (admin only)
- Profile management

### Role Management
Hierarchical roles ordered by level:

| Level | Role |
|-------|------|
| 1 | CEO |
| 2 | CTO, CFO, COO |
| 3 | Manager |
| 4 | Team Lead |
| 5 | Senior Developer, Developer, HR Specialist |
| 6 | Junior Developer |
| 7 | Employee |

### Task Board
- Kanban board (To Do → In Progress → In Review → Done)
- Create / update / delete tasks
- Assign tasks to employees
- Hierarchical sub-tasks
- My Tasks view (assigned to me / created by me)

---

## Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8+ (host: 10.60.169.217)
- Redis (for verification codes)
- Nginx

### 1. Configure email (create `.env` or export vars)

```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
export MAIL_FROM=noreply@hr-system.com
```

### 2. Start Backend

```bash
cd backend
./mvnw spring-boot:run
```

Backend runs at `http://localhost:8080/api`

**Swagger UI**: `http://localhost:8080/api/swagger-ui.html`

### 3. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at `http://localhost:3000`

### 4. Start Nginx

```bash
nginx -c $(pwd)/nginx/nginx.conf
```

Application accessible at `http://localhost:80`

---

## Default Admin Account

| Field | Value |
|-------|-------|
| Email | admin@hr-system.com |
| Password | Admin@123456 |

> Change this password immediately after first login!

---

## API Endpoints

### Authentication (public)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/send-verification` | Send/resend verification code |
| POST | `/api/auth/verify-email` | Verify email with code |
| POST | `/api/auth/login` | Login, returns JWT |
| POST | `/api/auth/refresh` | Refresh access token |

### Users (authenticated)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/users` | List all employees (paginated) |
| GET | `/api/users/me` | Current user profile |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Remove user (admin) |
| GET | `/api/users/org-chart` | Org chart data |

### Tasks (authenticated)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/tasks/kanban` | Kanban board data |
| GET | `/api/tasks/my-tasks` | Tasks assigned to me |
| GET | `/api/tasks/assigned-by-me` | Tasks I created |
| POST | `/api/tasks` | Create task |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |

---

## Configuration Reference (`application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:mysql://10.60.169.217:3306/hr_management
    username: root
    password: root123
  mail:
    host: ${MAIL_HOST}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000      # 24 hours
  refresh-expiration: 604800000  # 7 days
```

---

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `MAIL_HOST` | SMTP server host | smtp.gmail.com |
| `MAIL_PORT` | SMTP port | 587 |
| `MAIL_USERNAME` | SMTP username | — |
| `MAIL_PASSWORD` | SMTP app password | — |
| `MAIL_FROM` | Sender email | noreply@hr-system.com |
| `JWT_SECRET` | JWT signing secret | (see config) |
| `REDIS_HOST` | Redis host | 127.0.0.1 |
| `REDIS_PORT` | Redis port | 6379 |
| `FRONTEND_URL` | Frontend origin for CORS | http://localhost:3000 |
