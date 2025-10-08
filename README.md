# RENTOPS-AI
Modern Java Swing application for rental car operations with a clean DAO/MVC structure and future-friendly AI/NLP integration points. **(ACADEMIC PROJECT)**

---

## Overview
RENTOPS-AI is a desktop app that helps manage cars, users, and bookings. It uses:
- Java Swing for the UI
- JDBC for database access (MySQL/MariaDB)
- Clear layering: models → DAO → services → UI

Recent work includes:
- New field total_km_driven for cars (schema + DAO + data)
- Inventory expansion (now 20 cars, 19 available)
- Utilities to run SQL scripts and ad‑hoc queries from the command line
- Persistent login sessions via a user_sessions table

AI features are planned and kept decoupled via interfaces so they can be added without touching core flows.

---

## Features
- Fleet management (add/update/list cars, availability)
- User and admin roles; simple dashboards
- Bookings (basic CRUD in DAO layer)
- Secure auth (salted password hashing) with optional persistent sessions
- Utilities: SqlScriptRunner and DbQueryRunner for DB tasks

---

## Tech Stack
- Java 11+
- Swing (desktop UI)
- JDBC with MySQL/MariaDB
- MySQL Connector/J 9.4.0 (mysql-connector-j-9.4.0.jar in project root)

Note: This project currently builds without Maven/Gradle. Commands below show manual compile/run steps for Windows (PowerShell/CMD).

---

## Project Structure (actual)
```
RENTOPS-AI/
├── dao/                # DAO classes (CarDAO, UserDAO, BookingDAO, SessionDAO)
├── models/             # POJOs (Car, User, Booking, Payment, etc.)
├── services/           # AuthService, SessionManager
├── ui/                 # Swing panels and Main entry point
├── utils/              # DatabaseConnection, SqlScriptRunner, DbQueryRunner
├── config/             # db.properties
├── bin/                # Compiled .class output
├── *.sql               # Database setup/migration/data scripts
└── mysql-connector-j-9.4.0.jar
```

---

## Database
Schema name: rentops_ai

Key tables:
- users
- cars (includes total_km_driven INT NOT NULL DEFAULT 0)
- bookings
- user_sessions (for persistent login)
- ai_query_log (optional AI metrics/logging table)

Included SQL scripts:
- db_setup.sql – create schema and core tables (cars includes total_km_driven)
- add_sessions_table.sql – create user_sessions table and indexes
- alter_table.sql – migration helper to add total_km_driven to existing installs
- add_new_cars.sql – adds 13 ready-to-rent cars with mileage and pricing
- mysql_setup_commands.sql – convenience MySQL commands

Run scripts one of two ways:
1) In your MySQL/MariaDB client (Workbench/CLI): SOURCE path/to/script.sql
2) From this project using our runner:

```powershell
# Compile the utils once (if needed)
javac -d bin -cp ".;mysql-connector-j-9.4.0.jar" utils\*.java

# Run any SQL script
java -cp "bin;mysql-connector-j-9.4.0.jar" utils.SqlScriptRunner "db_setup.sql"
java -cp "bin;mysql-connector-j-9.4.0.jar" utils.SqlScriptRunner "add_sessions_table.sql"
java -cp "bin;mysql-connector-j-9.4.0.jar" utils.SqlScriptRunner "alter_table.sql"
java -cp "bin;mysql-connector-j-9.4.0.jar" utils.SqlScriptRunner "add_new_cars.sql"
```

Ad‑hoc query helper:
```powershell
java -cp "bin;mysql-connector-j-9.4.0.jar" utils.DbQueryRunner "SELECT COUNT(*) FROM cars;"
```

Database config lives in config/db.properties:
```
db.url=jdbc:mysql://localhost:3306/rentops_ai?useSSL=false&allowPublicKeyRetrieval=true
db.user=<your_user>
db.password=<your_password>
```
Please do not commit real credentials.

### AI Logging Table (Optional)
If you enable AI logging (`AI_LOG_DB_ENABLE=true` and provide an API key), create this table:

```sql
CREATE TABLE ai_query_log (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	task VARCHAR(40),
	model VARCHAR(80),
	prompt_hash CHAR(64),
	prompt_chars INT,
	response_chars INT,
	latency_ms BIGINT,
	success TINYINT(1),
	error_type VARCHAR(120),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_ai_query_log_task ON ai_query_log(task);
CREATE INDEX idx_ai_query_log_model ON ai_query_log(model);
```


---

## Build and Run (Windows)
From a PowerShell or CMD prompt in the project root:

```powershell
# 1) Compile
javac -d bin -cp ".;mysql-connector-j-9.4.0.jar" dao\*.java models\*.java services\*.java ui\*.java utils\*.java

# 2) Run
java -cp "bin;mysql-connector-j-9.4.0.jar" ui.Main
```

If you see driver or connection errors:
- Ensure mysql-connector-j-9.4.0.jar exists in the project root
- Verify config/db.properties points to a running MySQL/MariaDB instance
- Confirm the rentops_ai schema exists (run db_setup.sql)

---

## Notes on Authentication
- Passwords are stored as **salted hashes**; never store plain text passwords
- The seed admin in db_setup.sql is a placeholder; create users via the app’s Register flow or insert a properly hashed password
- Session persistence uses user_sessions; you can disable by not creating that table

---

## License
MIT — see LICENSE

---

## AI Features (In Progress)

Implemented slices:
1. Intent Extraction (heuristic + optional LLM)
2. Logging & Metrics (per task/model; persisted to `ai_query_log` if enabled)
3. Summarization Pipeline (sentence split → chunk → per-chunk summary → merged summary)

### Summarization Components (`com.rentops.ai.summarize`)
- `SentenceSplitter` – naive regex-based splitter.
- `Chunker` – groups sentences into size-bounded chunks (target char size, preserves sentence boundaries).
- `ChunkSummarizerService` – heuristic (first sentence + key capitalized tokens & numbers) or LLM (task `CHUNK_SUMMARY`).
- `MergeSummarizerService` – heuristic (top distinct sentences) or LLM (task `MERGE_SUMMARY`).
- `SummarizationPipeline` – orchestrates and returns final summary plus metadata (chunk count, average chunk length, elapsed ms, individual chunk summaries).

Heuristic mode is used when AI is disabled or an LLM error occurs (graceful fallback).

Example (conceptual):
```java
var chunkSummarizer = new ChunkSummarizerService(loggingClient, aiEnabled);
var mergeSummarizer = new MergeSummarizerService(loggingClient, aiEnabled);
var pipeline = new SummarizationPipeline(800, chunkSummarizer, mergeSummarizer);
var result = pipeline.summarize(longReportText);
System.out.println(result.summary());
```

Metrics & Logging: When using a `LoggingLlmClient`, tasks `CHUNK_SUMMARY` and `MERGE_SUMMARY` appear in in-memory metrics and (if enabled) DB logs.
