# Copilot Instructions for RENTOPS-AI

## Project Overview
RENTOPS-AI is a Java Swing desktop application for managing rental cars, users, admins, and bookings, with planned AI-powered insights. The backend uses MariaDB, accessed via JDBC (MariaDB Connector/J).

## Architecture & Patterns
- **MVC Structure:** Separate GUI (Swing), business logic, and DB operations. DAO classes for `users`, `cars`, and `bookings`.
- **Database Utility:** All DB connections handled in a dedicated utility class. Use connection pooling and avoid hardcoded credentials (read from config file).
- **Prepared Statements:** Always use `PreparedStatement` for DB queries to prevent SQL injection.
- **Role-Based Dashboards:** Distinct UI flows for users and admins. Admins manage cars/users/bookings; users search, book, and view history.
- **Password Security:** Hash passwords before storing in DB.
- **AI/NLP Integration:** Design modular interfaces for future AI features (NLP queries, analytics). Keep AI code decoupled from core app logic.

## Developer Workflows
- **Build:** Use standard Java build tools (e.g., `javac`, IDE build). No custom build scripts detected.
- **Run:** Launch main dashboard via Swing entry point (e.g., `Main.java`).
- **Database Setup:**
  - Schema: `rentops_ai`
  - Tables: `users`, `cars`, `bookings` (see prompt_readme.md for columns)
  - Use MariaDB Connector/J for JDBC connectivity.
- **Config:** Store DB credentials and config in a separate file (not hardcoded).

## Conventions & Examples
- **DAO Example:**
  - Each entity (`UserDAO`, `CarDAO`, `BookingDAO`) encapsulates CRUD and query logic.
- **UI Example:**
  - Use `JTable` for data display, `JOptionPane` for alerts.
  - Modern layouts (avoid absolute positioning).
- **Validation:**
  - Validate all user inputs before DB operations.
- **Extensibility:**
  - AI/NLP modules should be pluggable (interface-driven, e.g., `NLPService`).

## Key Files & Directories
- `prompt_readme.md`: Project requirements, schema, and guidelines.
- `.github/copilot-instructions.md`: AI agent instructions (this file).
- Future: Expect `src/` for Java code, `config/` for DB config, and DAO classes for each entity.

## Integration Points
- **MariaDB:** JDBC via MariaDB Connector/J.
- **AI/NLP:** Plan for Java or Python microservice integration (modular, interface-based).

---

**For AI agents:**
- Follow MVC and DAO patterns as described.
- Reference `prompt_readme.md` for schema and workflow details.
- Keep code modular, secure, and extensible for future AI features.
- Use examples from existing files when possible.
