@echo off
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
set "SOURCE_DIR=%SCRIPT_DIR%src\main\java\com\mouaad\pacman"
set "RESOURCES_DIR=%SCRIPT_DIR%src\main\resources\com\mouaad\pacman"
set "TARGET_DIR=%SCRIPT_DIR%target\classes\com\mouaad\pacman"

REM --- CLEAN STEP ---
if exist "%SCRIPT_DIR%target" rd /s /q "%SCRIPT_DIR%target"
mkdir "%TARGET_DIR%"

echo Compiling Java files...
javac -d "%SCRIPT_DIR%target\classes" "%SOURCE_DIR%\*.java"

REM --- RESOURCE STEP ---
echo Copying images...
copy "%RESOURCES_DIR%\*.png" "%TARGET_DIR%" >nul

echo Build complete!
java -cp "%SCRIPT_DIR%target\classes" com.mouaad.pacman.Main