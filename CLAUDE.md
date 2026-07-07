# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

东软颐养中心管理系统 — an elderly care facility management platform. Full-stack Java/Spring Boot microservices backend + Vue 3 frontend, registered via Nacos, routed through a Spring Cloud Gateway.

## Build & Run

**Prerequisites:** Java 17+, Maven 3.8+, Node.js 18+, MySQL 8.0, Nacos, RabbitMQ

**Database setup:**
```bash
mysql -u root -p -e "CREATE DATABASE neusoft_elderly_care CHARACTER SET utf8mb4;"
mysql -u root -p neusoft_elderly_care < database/neusoft_elderly_care.sql
```

**Backend** (from `backend/`):
```bash
mvn clean package -DskipTests          # build all modules
mvn clean package -pl elderlycare-meal # build single module
# Each module has its own *Application.java main class
```

**Frontend** (from `frontend/`):
```bash
npm install
npm run dev      # dev server on :5173, proxies /api -> :8080
npm run build    # production build
```

**Backend ports:** Gateway :8080, Auth :8081, AI :8090, other modules registered via Nacos.

## Architecture

### Backend Modules (12 Maven modules in `backend/`)

```
elderlycare-common      ← shared layer: entities, BaseEntity, ApiResponse, BaseCrudController,
                          SecurityConfig, JWT, Feign clients, RabbitMQ config, utilities
elderlycare-gateway     ← Spring Cloud Gateway, single entry point, routes by path prefix
elderlycare-auth        ← login/register, JWT token issuance
elderlycare-user        ← sys_user CRUD (admin/manager/nurse/user roles)
elderlycare-customer    ← customer CRUD + Aliyun OSS file upload
elderlycare-bed         ← bed management
elderlycare-checkin     ← check-in/check-out/outing registration (depends on customer + bed)
elderlycare-meal        ← meal planning per customer
elderlycare-service     ← care services, service purchases, service relations
elderlycare-nursing     ← nursing levels, items, records, areas
elderlycare-dashboard   ← aggregated statistics via Feign calls to other modules
elderlycare-ai          ← RAG engine + MiMo LLM integration, RabbitMQ event consumer
```

**Module dependency rule:** All modules depend on `common`. `checkin` depends on `customer` + `bed`. `dashboard` aggregates via Feign. `ai` depends on `auth` + RabbitMQ.

**Gateway route mapping** (`elderlycare-gateway/src/main/resources/application.yml`):
`/api/auth/**` → auth, `/api/users/**` → user, `/api/customers/**` → customer, `/api/beds/**` → bed, `/api/check-ins/**` → checkin, `/api/meals/**` → meal, `/api/nursing-*/**` → nursing, `/api/ai/**` → ai, etc.

### Frontend (`frontend/src/`)

**Configuration-driven CRUD pattern:** A single `CrudPage.vue` serves all 13+ business modules. To add a new CRUD module:
1. Add field config in `views/moduleConfig.js`
2. Add route + menu entry in `router/index.js`
3. No new Vue file needed

**Key files:**
- `api/crud.js` — generic CRUD API factory (pageApi, createApi, updateApi, deleteApi)
- `utils/request.js` — Axios instance with JWT interceptor, auto-attaches Bearer token
- `stores/user.js` — Pinia store for auth state
- `router/index.js` — routes, menu groups, role-based navigation guards
- `views/ai/` — AI-specific pages (AiChat, RagQuery, KnowledgeBase, HealthAnalysis, CareRecommendation)

## Key Design Patterns

- **BaseCrudController<T>:** Backend controllers extend this for zero-code CRUD (page/detail/create/update/delete). Override only what needs custom logic.
- **BaseEntity:** All entities inherit id, createTime, updateTime, deleted. Logical delete via `@TableLogic`. Timestamps auto-filled by MyBatis-Plus `MetaObjectHandler`.
- **Three-layer auth:** URL-level (SecurityConfig), method-level (`@PreAuthorize`), frontend (route guards + menu filtering by role).
- **AI RAG pipeline:** KnowledgeBaseService chunks docs (500 chars, 50 overlap) → Embedding API → SimpleVectorStore (ConcurrentHashMap). RagService vectorizes query → cosine similarity Top-K → injects into system prompt → LLM call. Falls back to direct LLM when knowledge base is empty.
- **AI graceful degradation:** Backend throws on LLM failure → GlobalExceptionHandler returns 500. Frontend wraps all AI calls with `withMock` → catch block serves demo data.

## Database

MySQL 8.0, InnoDB, utf8mb4. Schema at `database/neusoft_elderly_care.sql`. All tables use logical delete (`deleted` tinyint). 12 business tables total.

**Demo accounts** (auto-created): Admin 13800000000 / Manager 13800000001 / User 13800000002, all password `123456`.

## Shared Config

`backend/elderlycare-common/src/main/resources/application-common.yml` — MySQL, Nacos, RabbitMQ, JWT, OSS credentials. Included by all modules via Spring profiles.

## API Documentation

Knife4j (OpenAPI 3) enabled — visit `http://localhost:8080/doc.html` when backend is running. Postman collection at `postman/collection.json`.
