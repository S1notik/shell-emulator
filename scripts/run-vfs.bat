@echo off
chcp 65001 >nul
cd /d "%~dp0.."
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac --release 21 -d out @sources.txt
del sources.txt

echo 1. Minimalnaya VFS
java -cp out Main --vfs vfs-samples\minimal --script scripts\stage3-all.txt

echo 2. Neskolko faylov
java -cp out Main --vfs vfs-samples\several-files --script scripts\stage3-all.txt

echo 3. Derevo iz chetyreh urovney
java -cp out Main --vfs vfs-samples\deep-tree --script scripts\stage3-all.txt

echo 4. Bez VFS
java -cp out Main --script scripts\stage3-all.txt

echo 5. Nesushchestvuyushchaya direktoriya
java -cp out Main --vfs vfs-samples\no-such-dir

echo 6. Istochnik ne direktoriya
java -cp out Main --vfs vfs-samples\minimal\readme.txt

echo Gotovo.
