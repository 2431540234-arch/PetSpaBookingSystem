#!/bin/bash
# Quick Start Guide for PetSpa Backend

## Prerequisite Check
echo "Checking prerequisites..."

# Check Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -1)
    echo "✅ Java found: $JAVA_VERSION"
else
    echo "❌ Java not found. Please install Java 17 or higher."
    exit 1
fi

# Check Maven
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -v | head -1)
    echo "✅ Maven found: $MVN_VERSION"
else
    echo "❌ Maven not found. Please install Maven 3.8+."
    exit 1
fi

## Database Setup
echo ""
echo "Setting up database..."
echo "Make sure MySQL is running on localhost:3306"
echo ""
echo "Run this in your MySQL client:"
echo "---"
echo "CREATE DATABASE petspa CHARACTER SET utf8mb4;"
echo "---"
echo ""
read -p "Have you created the database? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Please create the database first."
    exit 1
fi

## Configuration
echo ""
echo "Updating configuration..."
echo "File: backend/src/main/resources/application.yml"
echo ""
echo "Update these values if needed:"
echo "  - datasource.url: jdbc:mysql://localhost:3306/petspa"
echo "  - datasource.username: root"
echo "  - datasource.password: your_password"
echo ""
read -p "Have you updated application.yml? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Please update application.yml first."
    exit 1
fi

## Build and Run
echo ""
echo "Building backend..."
cd backend
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Build successful!"
    echo ""
    echo "Starting backend..."
    mvn spring-boot:run
else
    echo ""
    echo "❌ Build failed. Check the errors above."
    exit 1
fi

