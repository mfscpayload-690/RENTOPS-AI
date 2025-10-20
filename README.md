# RENTOPS-AI

Modern car rental management system built with Java Swing, featuring role-based dashboards, secure authentication, and persistent session management.

## Features

- **Fleet Management**: Add, update, and track vehicle inventory with mileage monitoring
- **User Management**: Role-based access control (Admin/User) with secure authentication
- **Booking System**: Complete rental lifecycle management (create, update, track bookings)
- **Session Persistence**: Automatic login restoration across application restarts
- **Password Reset**: Self-service password recovery with secure hash regeneration
- **Modern UI**: Dark-themed interface with FlatLaf, responsive layouts, and smooth animations

## Screenshots

### Login Panel
<img width="1323" height="889" alt="image" src="https://github.com/user-attachments/assets/77d6a81b-c8a6-496c-9555-c50f50570609" />


### Admin Dashboard
<img width="1324" height="892" alt="image" src="https://github.com/user-attachments/assets/4a7be872-796f-4747-9884-576254450135" />


### User Dashboard
<img width="1316" height="888" alt="image" src="https://github.com/user-attachments/assets/402bd633-9e22-4ac8-bf2b-34ae55336fe3" />



## Tech Stack

- **Java**: 25 (compatible with Java 11+)
- **Build Tool**: Maven 3.9.11
- **UI Framework**: Java Swing with FlatLaf modern theme
- **Database**: MySQL/MariaDB
- **Security**: SHA-256 password hashing with unique salts
- **Architecture**: MVC pattern with DAO layer

## Project Structure

```
RENTOPS-AI/
 dao/                    # Data Access Objects
 models/                 # Domain models
 services/               # Business logic layer
 ui/                     # Swing UI components
    components/         # Reusable UI components
 utils/                  # Utilities and helpers
 config/                 # Configuration files
 sql/                    # Database scripts
    seeds/             # Sample data
    migrations/        # Schema updates
    maintenance/       # Utility scripts
 docs/                   # Documentation
```

## Quick Start

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- MySQL/MariaDB 5.7+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/RENTOPS-AI.git
   cd RENTOPS-AI
   ```

2. **Configure database**
   
   Edit `config/db.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/rentops_ai
   db.user=your_username
   db.password=your_password
   ```

3. **Initialize database**
   ```bash
   mysql -u root -p < sql/seeds/db_setup.sql
   ```

4. **Build and run**
   ```bash
   mvn clean package
   mvn exec:java
   ```

## Database Schema

**Core Tables:**
- `users` - User accounts with role-based permissions
- `cars` - Vehicle inventory with mileage tracking
- `bookings` - Rental transactions
- `user_sessions` - Persistent login sessions

## Security

- **Password Storage**: SHA-256 hashing with unique per-user salts
- **Session Management**: Secure token-based persistent sessions
- **Password Reset**: Self-service recovery with validation
- **SQL Injection**: Parameterized queries via PreparedStatements

## Documentation

- [Admin Dashboard Guide](docs/ADMIN_DASHBOARD_GUIDE.md)
- [Password Reset Feature](docs/PASSWORD_RESET_FEATURE.md)
- [Theming Guide](docs/THEMING_GUIDE.md)

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Academic Project

This is an academic project developed for educational purposes.
