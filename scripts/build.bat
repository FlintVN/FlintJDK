@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "ROOT=%~dp0.."
for %%I in ("%ROOT%") do set "ROOT=%%~fI"
set "SOURCE_ROOT=%ROOT%\src"
set "MANIFEST=%ROOT%\META-INF\MANIFEST.MF"
set "OUTPUT_ROOT=%ROOT%\bin"
set "RUN_ROOT=%OUTPUT_ROOT%\run"
set "DEV_ROOT=%OUTPUT_ROOT%\dev"

if /i "%~1"=="--clean" (
  if exist "%OUTPUT_ROOT%" rmdir /s /q "%OUTPUT_ROOT%"
  echo Clean complete.
  exit /b 0
)
if not "%~1"=="" (echo Usage: %~nx0 [--clean]>&2 & exit /b 2)

where javac >nul 2>nul || (echo javac was not found in PATH. Install JDK 17 or newer.>&2 & exit /b 1)
where jar >nul 2>nul || (echo jar was not found in PATH. Install JDK 17 or newer.>&2 & exit /b 1)
if not exist "%MANIFEST%" (echo Common manifest not found: %MANIFEST%>&2 & exit /b 1)
for /f "tokens=2 delims=. " %%V in ('javac -version 2^>^&1') do set "JAVAC_MAJOR=%%V"
if not defined JAVAC_MAJOR (echo Unable to detect the JDK version.>&2 & exit /b 1)
if !JAVAC_MAJOR! LSS 17 (echo JDK 17 or newer is required; found version !JAVAC_MAJOR!.>&2 & exit /b 1)

set "MODULES="
set "MODULE_LIST="
for /d %%D in ("%SOURCE_ROOT%\*") do (
  if exist "%%~fD\module-info.java" (
    set "MODULES=!MODULES! %%~nxD"
    if defined MODULE_LIST (set "MODULE_LIST=!MODULE_LIST!,%%~nxD") else set "MODULE_LIST=%%~nxD"
  )
)
if not defined MODULE_LIST (echo No Java modules found under: %SOURCE_ROOT%>&2 & exit /b 1)

if exist "%OUTPUT_ROOT%" rmdir /s /q "%OUTPUT_ROOT%"
mkdir "%RUN_ROOT%" "%DEV_ROOT%" 2>nul

echo Building runtime modules: !MODULE_LIST!
javac -Xlint:all -XDstringConcat=inline --release 17 -encoding UTF-8 -d "%RUN_ROOT%" --module "!MODULE_LIST!" --module-source-path "%SOURCE_ROOT%"
if errorlevel 1 exit /b 1

echo Building development modules: !MODULE_LIST!
javac -g -Xlint:all -XDstringConcat=inline --release 17 -encoding UTF-8 -d "%DEV_ROOT%" --module "!MODULE_LIST!" --module-source-path "%SOURCE_ROOT%"
if errorlevel 1 exit /b 1

for %%M in (!MODULES!) do (
  jar cf0m "%RUN_ROOT%\%%M.jar" "%MANIFEST%" -C "%RUN_ROOT%\%%M" .
  if errorlevel 1 exit /b 1
  xcopy "%SOURCE_ROOT%\%%M" "%DEV_ROOT%\%%M\src\" /E /I /Q /Y >nul
  if errorlevel 1 exit /b 1
  jar cfm "%DEV_ROOT%\%%M.jar" "%MANIFEST%" -C "%DEV_ROOT%\%%M" .
  if errorlevel 1 exit /b 1
)

echo Build complete: %OUTPUT_ROOT%
endlocal
