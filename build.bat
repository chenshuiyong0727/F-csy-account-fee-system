@echo off
chcp 65001 > nul
setlocal
title Build account-fee-system

cd /d "%~dp0"

if exist "D:\Java\21\bin\java.exe" (
    set "JAVA_HOME=D:\Java\21"
    set "PATH=D:\Java\21\bin;%PATH%"
)

echo [1/5] Install frontend dependencies...
cd frontend
call npm install
if errorlevel 1 (
    echo Frontend dependency install failed.
    pause
    exit /b 1
)

echo [2/5] Build frontend...
call npm run build
if errorlevel 1 (
    echo Frontend build failed.
    pause
    exit /b 1
)

echo [3/5] Copy frontend dist to backend static...
cd ..
if exist "backend\src\main\resources\static" rmdir /s /q "backend\src\main\resources\static"
mkdir "backend\src\main\resources\static"
xcopy "frontend\dist\*" "backend\src\main\resources\static\" /E /I /Y
if errorlevel 1 (
    echo Copy frontend static files failed.
    pause
    exit /b 1
)

echo [4/5] Build backend jar...
cd backend
call mvn clean package -DskipTests
if errorlevel 1 (
    echo Backend build failed.
    pause
    exit /b 1
)

echo [5/5] Copy jar to project root...
cd ..
copy /Y "backend\target\account-fee-system.jar" "account-fee-system.jar"
if errorlevel 1 (
    echo Copy jar failed.
    pause
    exit /b 1
)

echo.
echo Build finished: account-fee-system.jar
echo Import sql\init.sql before first run, then run start.bat.
echo.
pause
