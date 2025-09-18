# How to Run RENTOPS-AI

This guide provides step-by-step instructions for running the RENTOPS-AI Java Swing application.

## Prerequisites

1. **Java Development Kit (JDK) 11+** installed on your system
2. **MariaDB Database** installed and running
3. Basic knowledge of running commands in terminal/command prompt

## Setup Steps

### 1. Database Setup

1. **Create and initialize the database:**
   - Open MariaDB/MySQL client: `mysql -u root -p`
   - Run the SQL script to create the database schema:
     ```
     source d:\You know what PROJECTZZ\RENTOPS-AI\db_setup.sql
     ```
   - Or run the script using a database management tool like phpMyAdmin, MySQL Workbench, etc.

2. **Update database connection properties** (if needed):
   - Open `config/db.properties`
   - Modify the following properties according to your database configuration:
     ```
     db.url=jdbc:mariadb://localhost:3306/rentops_ai
     db.user=root
     db.password=root
     ```
   - Replace `root` with your actual MariaDB username and password

### 2. Run the Application

#### Option 1: Using the batch file (Windows)

1. Simply double-click on `run.bat` in the project folder
2. This script will:
   - Download the MariaDB JDBC driver (if not present)
   - Compile all Java files
   - Run the application

#### Option 2: Manual compilation and execution

1. Open Command Prompt or PowerShell
2. Navigate to the project directory:
   ```
   cd "d:\You know what PROJECTZZ\RENTOPS-AI"
   ```

3. Create bin directory (if it doesn't exist):
   ```
   mkdir bin
   ```

4. Download MariaDB driver (if not already present):
   ```
   curl -o lib\mariadb-java-client.jar https://downloads.mariadb.com/Connectors/java/connector-java-3.0.9/mariadb-java-client-3.0.9.jar
   ```

5. Compile the Java files:
   ```
   javac -d bin -cp ".;lib\mariadb-java-client.jar" dao\*.java models\*.java services\*.java ui\*.java utils\*.java
   ```

6. Run the application:
   ```
   java -cp "bin;lib\mariadb-java-client.jar" ui.Main
   ```

## Troubleshooting

1. **Database Connection Issues:**
   - Verify MariaDB is running
   - Check credentials in `config/db.properties`
   - Ensure the database `rentops_ai` exists
   - Confirm port 3306 is not blocked by firewall

2. **Compilation Errors:**
   - Make sure JDK is properly installed
   - Verify JAVA_HOME environment variable is set correctly

3. **Runtime Errors:**
   - Check if MariaDB JDBC driver is in the lib folder
   - Ensure all tables are created properly

## Default Login

- **Admin User:**
  - Username: admin
  - Password: admin123

- **Regular User:**
  - You can register a new user from the login screen