@echo off
echo MySQL Password Reset Instructions
echo ===================================
echo.
echo 1. Stop MySQL service if running:
echo    net stop MySQL80
echo.
echo 2. Start MySQL in safe mode (skip grant tables):
echo    mysqld --skip-grant-tables --skip-networking
echo.
echo 3. In another terminal, connect without password:
echo    mysql -u root
echo.
echo 4. Reset the password:
echo    USE mysql;
echo    ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_new_password';
echo    FLUSH PRIVILEGES;
echo    EXIT;
echo.
echo 5. Restart MySQL normally:
echo    net start MySQL80
echo.
pause