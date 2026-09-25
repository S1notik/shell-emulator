@echo off
chcp 65001 >nul
cd /d "%~dp0"
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac --release 21 -d out @sources.txt
del sources.txt
java -cp out Main
