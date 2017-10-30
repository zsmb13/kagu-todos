@echo off
tasklist /FI "IMAGENAME eq mongod.exe" 2>NUL | find /I /N "mongod.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo "Mongodb is already up"
) else (
    echo "Starting mongodb"
    start cmd /k "mongod.exe"
)

tasklist /FI "windowtitle eq KaguTodoBackend - java  -jar ../backend/build/libs/backend-fat.jar" 2>NUL | find /I /N "cmd.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo "Killing backend"
    taskkill /fi "windowtitle eq KaguTodoBackend - java  -jar ../backend/build/libs/backend-fat.jar"
    echo "Restarting backend"
    start "KaguTodoBackend" cmd /k "java -jar ../backend/build/libs/backend-fat.jar"
) else (
    echo "Starting backend"
    start "KaguTodoBackend" cmd /k "java -jar ../backend/build/libs/backend-fat.jar"
)

echo "Opening frontend"
start chrome /new-window http://localhost:63342/kagu-todos/web/
