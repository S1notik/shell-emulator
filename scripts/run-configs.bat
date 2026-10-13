@echo off
chcp 65001 >nul
cd /d "%~dp0.."
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac --release 21 -d out @sources.txt
del sources.txt

echo 1. Bez parametrov
java -cp out Main

echo 2. Tolko VFS
java -cp out Main --vfs vfs-samples\minimal

echo 3. Tolko skript
java -cp out Main --script scripts\stage2-basic.txt

echo 4. Oba parametra
java -cp out Main --vfs vfs-samples\minimal --script scripts\stage2-basic.txt

echo 5. Skript s oshibkami
java -cp out Main --script scripts\stage2-errors.txt

echo 6. Nesushchestvuyushchiy skript
java -cp out Main --script scripts\no-such-file.txt

echo 7. Neizvestnyy parametr
java -cp out Main --wrong value

echo 8. Parametr bez znacheniya
java -cp out Main --vfs

echo Gotovo.
