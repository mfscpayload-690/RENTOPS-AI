# RENTOPS-AI Launcher Script
# This script sets up the environment and runs the RENTOPS-AI application

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   RENTOPS-AI - Starting Application" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host " "

# Set JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "[1/3] Checking Java installation..." -ForegroundColor Yellow
java -version
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Java not found! Please check JAVA_HOME path." -ForegroundColor Red
    pause
    exit 1
}

Write-Host ""
Write-Host "[2/3] Checking MySQL connection..." -ForegroundColor Yellow
$mysqlService = Get-Service -Name MySQL80 -ErrorAction SilentlyContinue
if ($mysqlService.Status -ne 'Running') {
    Write-Host "WARNING: MySQL80 service is not running!" -ForegroundColor Red
    Write-Host "Please start MySQL80 service with: net start MySQL80" -ForegroundColor Yellow
    pause
    exit 1
}
Write-Host "MySQL is running OK" -ForegroundColor Green

Write-Host " "
Write-Host "[3/3] Launching RENTOPS-AI..." -ForegroundColor Yellow
Write-Host "Default Admin Login:" -ForegroundColor Cyan
Write-Host "  Username: admin" -ForegroundColor White
Write-Host "  Password: admin123" -ForegroundColor White
Write-Host " "

# Run the application
java -cp "target/classes;target/dependency/*;lib/*;mysql-connector-j-9.4.0.jar" ui.Main

Write-Host " "
Write-Host "Application closed." -ForegroundColor Yellow
pause
