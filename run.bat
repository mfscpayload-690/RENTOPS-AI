@echo off
echo RENTOPS-AI Project Launcher
echo ==========================

REM Check if MariaDB driver exists
if not exist "lib\mariadb-java-client.jar" (
  echo Downloading MariaDB JDBC Driver...
  powershell -Command "Invoke-WebRequest -Uri https://downloads.mariadb.com/Connectors/java/connector-java-3.0.9/mariadb-java-client-3.0.9.jar -OutFile lib\mariadb-java-client.jar"
  if %ERRORLEVEL% NEQ 0 (
    echo Failed to download MariaDB driver. Please download it manually and place it in the lib directory.
    pause
    exit /b 1
  )
)

echo Compiling project...
if not exist "bin" mkdir bin

REM Compile all Java files
javac -d bin -cp ".;lib\mariadb-java-client.jar" dao\*.java models\*.java services\*.java ui\*.java utils\*.java

if %ERRORLEVEL% NEQ 0 (
  echo Compilation failed!
  pause
  exit /b 1
)

echo Running RENTOPS-AI...
java -cp "bin;lib\mariadb-java-client.jar" ui.Main

pause