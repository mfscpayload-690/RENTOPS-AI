@echo off
echo Starting MySQL Server manually...
echo.
echo Note: You may need to run this as Administrator
echo.

cd /d "C:\Program Files\MySQL\MySQL Server 8.0\bin"

echo Starting MySQL daemon...
mysqld --console --initialize-insecure --user=mysql

echo.
echo If you see errors above, MySQL might already be initialized.
echo Now starting MySQL server...

mysqld --console

pause