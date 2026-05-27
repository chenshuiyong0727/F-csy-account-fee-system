@echo off
chcp 65001 > nul
title 构建会计服务费收款登记系统

if exist "D:\Java\21\bin\java.exe" (
    set "JAVA_HOME=D:\Java\21"
    set "PATH=D:\Java\21\bin;%PATH%"
)

echo [1/5] 安装前端依赖...
cd frontend
call npm install
if errorlevel 1 (
    echo 前端依赖安装失败。
    pause
    exit /b 1
)

echo [2/5] 构建前端...
call npm run build
if errorlevel 1 (
    echo 前端构建失败。
    pause
    exit /b 1
)

echo [3/5] 复制前端 dist 到后端 static...
cd ..
if exist "backend\src\main\resources\static" rmdir /s /q "backend\src\main\resources\static"
mkdir "backend\src\main\resources\static"
xcopy "frontend\dist\*" "backend\src\main\resources\static\" /E /I /Y
if errorlevel 1 (
    echo 前端静态资源复制失败。
    pause
    exit /b 1
)

echo [4/5] 构建后端 jar...
cd backend
call mvn clean package -DskipTests
if errorlevel 1 (
    echo 后端构建失败。
    pause
    exit /b 1
)

echo [5/5] 复制 jar 到根目录...
cd ..
copy /Y "backend\target\account-fee-system.jar" "account-fee-system.jar"
if errorlevel 1 (
    echo jar 复制失败。
    pause
    exit /b 1
)

echo.
echo 构建完成：account-fee-system.jar
echo 请先导入 sql\init.sql 初始化数据库，然后运行 start.bat。
echo.
pause
