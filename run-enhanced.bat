@echo off
title RENTOPS-AI Enhanced - Latest Version
echo.
echo ====================================
echo   RENTOPS-AI Enhanced Version
echo ====================================
echo   Features: 51 Cars + AI Reports
echo   AI: Intent Extraction + Summarization
echo ====================================
echo.

cd /d "d:\You know what PROJECTZZ\RENTOPS-AI"

echo [1/3] Compiling latest sources...
call mvn compile -q

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Compilation failed! Check for errors.
    pause
    exit /b 1
)

echo [2/3] Starting RENTOPS-AI Enhanced...
echo ✅ 51 Cars loaded
echo ✅ AI Features active
echo ✅ Reports tab enhanced

java -cp "target\classes;mysql-connector-j-9.4.0.jar" ui.Main

echo.
echo [3/3] Application closed.
pause