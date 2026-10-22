@echo off
chcp 65001 >nul
cd /d "%~dp0.."
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac --release 21 -d out @sources.txt
del sources.txt

echo 1. Komandy etapa 4 na glubokom dereve
java -cp out Main --vfs vfs-samples\deep-tree --script scripts\stage4-all.txt

echo 2. Te zhe komandy bez VFS
java -cp out Main --script scripts\stage4-all.txt

echo Gotovo.
