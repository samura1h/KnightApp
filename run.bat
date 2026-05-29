@echo off
chcp 65001 > nul
set "JAVA_PATH=C:\Program Files\Java\jdk-26.0.1\bin\java.exe"
set "JAVAC_PATH=C:\Program Files\Java\jdk-26.0.1\bin\javac.exe"
set "FX_PATH=C:\javafx-sdk\lib"

set "CP_LIBS=C:\Users\nazar\.m2\repository\org\apache\logging\log4j\log4j-api\2.20.0\log4j-api-2.20.0.jar;C:\Users\nazar\.m2\repository\org\apache\logging\log4j\log4j-core\2.20.0\log4j-core-2.20.0.jar;C:\Users\nazar\.m2\repository\com\sun\mail\javax.mail\1.6.2\javax.mail-1.6.2.jar;C:\Users\nazar\.m2\repository\javax\activation\activation\1.1\activation-1.1.jar;C:\Users\nazar\.m2\repository\org\xerial\sqlite-jdbc\3.45.1.0\sqlite-jdbc-3.45.1.0.jar;C:\Users\nazar\.m2\repository\org\slf4j\slf4j-api\2.0.9\slf4j-api-2.0.9.jar;C:\Users\nazar\.m2\repository\org\slf4j\slf4j-nop\2.0.9\slf4j-nop-2.0.9.jar"
set "CP=out;%CP_LIBS%"

echo ==================================================
echo    KNIGHT ORDER MANAGEMENT SYSTEM v1.0 (GUI)
echo ==================================================

rem 1. Check JDK
if not exist "%JAVAC_PATH%" (
    echo [ERROR] Compiler not found at: %JAVAC_PATH%
    goto error
)
if not exist "%JAVA_PATH%" (
    echo [ERROR] Java runner not found at: %JAVA_PATH%
    goto error
)

rem 2. Prep dirs
if not exist "out" mkdir out
if not exist "out\gui" mkdir out\gui
copy /Y "src\gui\style.css" "out\gui\style.css" > nul
copy /Y "src\gui\logo.png" "out\gui\logo.png" > nul

rem 3. Compile
echo [1/2] Compiling...
"%JAVAC_PATH%" --module-path "%FX_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base --release 17 -cp "%CP_LIBS%" -d out -sourcepath src src/Main.java src/gui/GuiMain.java
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed.
    goto error
)

rem 4. Run
echo [2/2] Running...
"%JAVA_PATH%" --module-path "%FX_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "%CP%" Main %*
goto end

:error
echo.
echo Failed to run project. Please check JDK and JavaFX setup.
pause

:end
