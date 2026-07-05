@echo off
REM Quick Start Guide for PetSpa Backend (Windows)

echo.
echo =========================================
echo    PetSpa Backend - Quick Start (Windows)
echo =========================================
echo.

REM Check Java
echo Checking Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java not found. Please install Java 17 or higher.
    exit /b 1
)
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| find /v "version" ^| findstr /r "[0-9]"') do set JAVA_VERSION=%%i
echo ✅ Java found: %JAVA_VERSION%
echo.

REM Check Maven
echo Checking Maven...
mvn -v >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven not found. Please install Maven 3.8+.
    exit /b 1
)
echo ✅ Maven found
echo.

REM Database Setup
echo =========================================
echo    Database Setup
echo =========================================
echo.
echo Make sure MySQL is running on localhost:3306
echo.
echo Run this in your MySQL client:
echo ---
echo CREATE DATABASE petspa CHARACTER SET utf8mb4;
echo ---
echo.
set /p db_ready="Have you created the database? (y/n): "
if /i not "%db_ready%"=="y" (
    echo Please create the database first.
    exit /b 1
)
echo.

REM Configuration Check
echo =========================================
echo    Configuration Check
echo =========================================
echo.
echo File: backend\src\main\resources\application.yml
echo.
echo Update these values if needed:
echo   - datasource.url: jdbc:mysql://localhost:3306/petspa
echo   - datasource.username: root
echo   - datasource.password: your_password
echo.
set /p config_ready="Have you updated application.yml? (y/n): "
if /i not "%config_ready%"=="y" (
    echo Please update application.yml first.
    exit /b 1
)
echo.

REM Build
echo =========================================
echo    Building Backend
echo =========================================
echo.
cd backend
mvn clean install -DskipTests

if %errorlevel% neq 0 (
    echo.
    echo ❌ Build failed. Check the errors above.
    exit /b 1
)

echo.
echo ✅ Build successful!
echo.
echo =========================================
echo    Starting Backend Server
echo =========================================
echo.
echo Server will start at: http://localhost:8080/api
echo.
mvn spring-boot:run

pause

