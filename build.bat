@echo off

echo "Cleaning project"
call gradlew -q clean

echo "Building backend jar"
call gradlew -q backend:shadowJar

echo "Assembling frontend"
call gradlew -q frontend:build
