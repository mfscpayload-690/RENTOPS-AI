@echo off
title RENTOPS-AI Application
echo =====================================
echo   RENTOPS-AI - Car Rental System
echo =====================================
echo.

REM Set JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-25
set PATH=%JAVA_HOME%\bin;%PATH%

echo [1/2] Checking Java...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java not found!
    pause
    exit /b 1
)

echo.
echo [2/2] Launching Application...
echo Default Admin Login:
echo   Username: admin
echo   Password: admin123
echo.

java -cp "target/classes;target/dependency/*;lib/*;mysql-connector-j-9.4.0.jar" ui.Main

echo.
echo Application closed.
pause
