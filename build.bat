@echo off
echo ============================================================
echo Building Simple Decision Maker APK
echo ============================================================

set JAVA_HOME=D:\Android\Android Studio\jbr
set GRADLE_USER_HOME=%USERPROFILE%\.gradle

echo.
echo [1/3] Checking Java...
"%JAVA_HOME%\bin\java.exe" -version
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java not found!
    exit /b 1
)
echo OK: Java found

echo.
echo [2/3] Downloading Gradle Wrapper...
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo Downloading gradle-wrapper.jar...
    powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.2-bin.zip' -OutFile '%TEMP%\gradle.zip'"
    if %ERRORLEVEL% NEQ 0 (
        echo ERROR: Failed to download Gradle
        exit /b 1
    )
    echo Downloaded successfully
) else (
    echo OK: Gradle wrapper exists
)

echo.
echo [3/3] Building APK...
call gradlew.bat assembleDebug --no-daemon
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ============================================================
    echo BUILD FAILED
    echo ============================================================
    echo.
    echo Please open Android Studio and build manually:
    echo 1. Open Android Studio
    echo 2. File ^> Open ^> Select: %CD%
    echo 3. Build ^> Build APK(s)
    echo.
    exit /b 1
)

echo.
echo ============================================================
echo BUILD SUCCESSFUL!
echo ============================================================
echo.
echo APK location: %CD%\app\build\outputs\apk\debug\app-debug.apk
echo.
