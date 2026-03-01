# DBMS-IATRS (Java)

Backend has been migrated from Python Flask to Java Spring Boot.

## Prerequisites
- Java 17+
- Maven 3.9+
- MySQL 8+

## 1) Create database
Run in MySQL:

```sql
CREATE DATABASE ats_db;
USE ats_db;
SOURCE schema.sql;
SOURCE seed.sql;
```

## 2) Configure environment
Copy `.env.example` values into your shell environment (or set system env vars):

- `DB_HOST`
- `DB_USER`
- `DB_PASSWORD`
- `DB_NAME`

PowerShell example:

```powershell
$env:DB_HOST='127.0.0.1'
$env:DB_USER='root'
$env:DB_PASSWORD='your_mysql_password'
$env:DB_NAME='ats_db'
```

## 3) Run backend

```powershell
mvn spring-boot:run
```

API runs on `http://127.0.0.1:5000`.

## 4) Open frontend
Spring serves the frontend pages directly:

- `http://127.0.0.1:5000/portal`
- `http://127.0.0.1:5000/dashboard.html`
- `http://127.0.0.1:5000/api-status.html`
- `http://127.0.0.1:5000/database-schema.html`

## API endpoints
- `GET /`
- `GET /jobs`
- `GET /jobs/{id}`
- `POST /jobs`
- `POST /apply`
- `GET /applications`
