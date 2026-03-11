@echo off
REM Run University ERP from project root. Place this script in scripts/ and run from repo root, or run from scripts/.
setlocal
cd /d "%~dp0\.."
set "ROOT=%CD%"
set "CP=%ROOT%\out;%ROOT%\lib\*;%ROOT%\resources"
set "SRC=%ROOT%\src\main\java"

if not exist "%ROOT%\config\config.properties" (
    echo ERROR: config\config.properties not found.
    echo Copy config\config.properties.example to config\config.properties and set your database credentials.
    exit /b 1
)
if not exist "%ROOT%\lib" (
    echo ERROR: lib folder not found. Add JARs: flatlaf, jbcrypt, mysql-connector-j.
    exit /b 1
)

echo Compiling...
if not exist "%ROOT%\out" mkdir "%ROOT%\out"
javac -encoding UTF-8 -d "%ROOT%\out" -cp "%ROOT%\lib\*" -sourcepath "%SRC%" "%SRC%\edu\univ\erp\Main.java"
if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)
echo Running...
java -cp "%CP%" edu.univ.erp.Main
endlocal
