# Intelligent Applicant Tracking and Recruitment System (IATS)

Desktop ATS built with Java Swing, Core Java service layer, MySQL, and JDBC.

## Implemented Modules
- User authentication with role support: `Admin` and `Recruiter`
- Job posting management (title, department, location, required skills)
- Applicant management (profile, resume URL/file path, resume text)
- Resume storage (`resume_url`, `resume_text`)
- Intelligent resume screening via keyword/skill matching
- Weighted skill matching (e.g., `Java:5, SQL:4, Communication:2`)
- Synonym recognition (e.g., `JS` -> `JavaScript`, `Spring Boot` -> `Spring`)
- Skill-gap analysis (matched skills vs missing skills)
- Auto status automation:
  - score >= 80 -> `Screening`
  - score < 35 -> `Rejected`
  - missing mandatory weighted skill (weight >= 5) -> `Rejected`
- Top candidate ranking (Top 10 per job by screening score)
- Application status tracking (`Applied`, `Screening`, `Interviewing`, `Rejected`, `Hired`)
- Interview scheduling (`Phone`, `Video`, `Onsite`) with 60-minute conflict detection
- Report generation (summary + detailed) and CSV export (Admin)

## Tech Stack
- Java 17+ (tested with Java 25)
- Java Swing (frontend)
- Core Java + JDBC (backend)
- MySQL 8+
- IntelliJ IDEA (recommended)

## Project Structure
- `src/main/java/com/iats/app/ui` - Swing UI screens
- `src/main/java/com/iats/app/service` - Core business logic
- `src/main/java/com/iats/app/repository` - JDBC repositories
- `src/main/java/com/iats/app/model` - domain models
- `src/main/resources/sql/schema.sql` - MySQL schema
- `src/main/resources/application.properties` - DB credentials

## MySQL Setup
1. Update DB credentials in `src/main/resources/application.properties`.
2. Run `src/main/resources/sql/schema.sql` in MySQL Workbench / IntelliJ Database tool.

Default keys:
- `db.url=jdbc:mysql://localhost:3306/iats_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- `db.username=root`
- `db.password=password`

## Run in IntelliJ IDEA
1. Open this folder as a project.
2. Ensure MySQL Connector dependency resolves from `pom.xml` (or keep local JAR in `lib/`).
3. Run `com.iats.app.Main`.

## Notes
- Application creation triggers automatic screening.
- You can enter job skills with explicit weights:
  - Example: `Java:5, Spring Boot:5, SQL:4, Communication:2`
- Admin users can export CSV reports to `reports/`.
- If upgrading from an older schema, re-run the SQL script to add new columns/tables (`role`, `required_skills`, `resume_text`, `ApplicationScreenings.missing_skills`).
