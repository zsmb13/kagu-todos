@echo off

echo "Assembling frontend"
call gradlew -q frontend:build

echo "Opening frontend"
start chrome /new-window http://localhost:63342/kagu-todos/web/
