@echo off
REM Build script for PacMan game
REM Compiles Java files and copies resources to target directory

setlocal enabledelayedexpansion

REM Get the directory where this batch file is located
set "SCRIPT_DIR=%~dp0"

set "SOURCE_DIR=%SCRIPT_DIR%src\main\java\com\mouaad\pacman"
set "RESOURCES_DIR=%SCRIPT_DIR%src\main\resources\com\mouaad\pacman"
set "TARGET_DIR=%SCRIPT_DIR%target\classes\com\mouaad\pacman"

REM Create target directory
if not exist "%TARGET_DIR%" mkdir "%TARGET_DIR%"

REM Compile Java files using for loop to handle wildcards correctly
echo Compiling Java files...
set "count=0"
for %%f in ("%SOURCE_DIR%\*.java") do (
    set "javac_files=!javac_files! "%%f""
    set /a count+=1
)

if %count% equ 0 (
    echo Error: No Java files found in %SOURCE_DIR%
    exit /b 1
)

javac -d "%SCRIPT_DIR%target\classes" !javac_files!
if errorlevel 1 (
    echo Compilation failed!
    exit /b 1
)

REM Copy resources
echo Copying image resources...
for %%f in ("%RESOURCES_DIR%\*.png") do (
    copy "%%f" "%TARGET_DIR%" >nul
)

echo Build complete! Output in: %TARGET_DIR%
echo.
echo To run the game use:
echo   java -cp %SCRIPT_DIR%target\classes com.mouaad.pacman.Main