@echo off
chcp 65001 >nul
cd /d "%~dp0"
if not exist out mkdir out
if not exist out-test mkdir out-test
dir /s /b src\*.java > sources.txt
javac --release 21 -d out @sources.txt
del sources.txt
dir /s /b tests\*.java > test-sources.txt
javac --release 21 -cp out -d out-test @test-sources.txt
del test-sources.txt
java -cp "out;out-test" parser.CommandParserTest
