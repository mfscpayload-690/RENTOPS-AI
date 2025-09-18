@echo off
title MySQL Setup for RENTOPS-AI
echo ========================================
echo MySQL Setup for RENTOPS-AI Application
echo ========================================
echo.

echo Step 1: Checking MySQL installation...
if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
    echo ✓ MySQL found at: C:\Program Files\MySQL\MySQL Server 8.0\
) else (
    echo ✗ MySQL not found. Please install MySQL Server 8.0 first.
    pause
    exit /b 1
)

echo.
echo Step 2: Checking MySQL service status...
sc query MySQL80 | find "RUNNING" >nul
if %errorlevel% == 0 (
    echo ✓ MySQL service is running
    goto :database_setup
) else (
    echo ⚠ MySQL service is not running
    echo Attempting to start MySQL service...
    net start MySQL80
    if %errorlevel% == 0 (
        echo ✓ MySQL service started successfully
    ) else (
        echo ✗ Failed to start MySQL service
        echo You may need to run this script as Administrator
        echo.
        echo To run as Administrator:
        echo 1. Right-click on this file
        echo 2. Select "Run as administrator"
        echo.
        pause
        exit /b 1
    )
)

:database_setup
echo.
echo Step 3: Setting up database...
echo.
echo Please enter your MySQL root password when prompted.
echo If you haven't set a password, try pressing Enter (empty password).
echo.

cd /d "C:\Program Files\MySQL\MySQL Server 8.0\bin"

echo Creating database and user...
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS rentops_ai; CREATE USER IF NOT EXISTS 'asus'@'localhost' IDENTIFIED BY '3005'; GRANT ALL PRIVILEGES ON rentops_ai.* TO 'asus'@'localhost'; FLUSH PRIVILEGES;"

if %errorlevel% == 0 (
    echo ✓ Database and user created successfully
) else (
    echo ✗ Failed to create database. Please check your MySQL password.
    pause
    exit /b 1
)

echo.
echo Step 4: Running database schema...
cd /d "%~dp0"
mysql -u asus -p3005 rentops_ai < db_setup.sql

if %errorlevel% == 0 (
    echo ✓ Database schema created successfully
) else (
    echo ✗ Failed to create database schema
    pause
    exit /b 1
)

echo.
echo ========================================
echo ✓ MySQL setup complete!
echo ========================================
echo.
echo Database: rentops_ai
echo Username: asus
echo Password: 3005
echo Host: localhost
echo Port: 3306
echo.
echo You can now run your Java application!
echo.
pause