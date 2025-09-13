# Project: Rentops-AI – Java Desktop Application
# Objective:
Develop a Java-based Rental Cars Management System with AI-powered insights.
The application should manage rental cars, users, admins, bookings, and provide
an AI-driven assistant for data insights (later stage).

# Tech Stack:
- Java (Swing for GUI, JDBC for DB connectivity)
- MariaDB as backend database
- JDBC Driver: MariaDB Connector/J
- Gradual integration of NLP/AI for advanced features

# Core Modules & Milestones:
1. **Phase 1: Basic Desktop App**
   - Create a Java Swing application with a main dashboard window.
   - Menu options: User Login, Admin Login, Exit.

2. **Phase 2: Database Setup**
   - Configure JDBC connection to MariaDB.
   - Create schema: `rentops_ai`.
   - Tables:
     - `users` (id, username, password, role [user/admin])
     - `cars` (id, brand, model, specs, availability, price_per_day)
     - `bookings` (id, user_id, car_id, start_date, end_date, status)

3. **Phase 3: Authentication**
   - User login & Admin login system.
   - Allow registration (default role = user).
   - Hash passwords before storing.

4. **Phase 4: Role-Based Dashboards**
   - **Admin Dashboard:** manage cars (add, update, delete, mark availability), view all bookings, manage users.
   - **User Dashboard:** search cars, check availability, view details/specs, book cars, view booking history.

5. **Phase 5: AI/NLP Integration**
   - Add AI-powered natural language queries (e.g., “Show me all available SUVs this weekend”).
   - Use lightweight NLP library in Java (or integrate with Python microservice if needed).
   - AI suggestions for pricing optimization, popular cars, etc.

# Coding Guidelines:
- Use MVC architecture where possible (separate GUI, business logic, DB operations).
- Keep DB connections in a separate utility class.
- Write reusable DAO (Data Access Object) classes for `users`, `cars`, `bookings`.
- Always validate user inputs (prevent SQL injection).
- For AI/NLP, design modular interface so it can be plugged later.

# Deliverables by Phase:
- Fully functional Swing app
- JDBC connectivity with MariaDB
- User/Admin login + dashboards
- Car rental workflows (CRUD + bookings)
- AI module (NLP assistant & smart analytics)

# Notes for Copilot:
- Generate clean, well-structured, commented Java code.
- Use JDBC best practices (PreparedStatement, connection pooling).
- Keep UI modern (Swing layouts, JTable for data, JOptionPane for alerts).
- Avoid hardcoding credentials (use config file).
