#!/usr/bin/env bash
cd "$(dirname "$0")/.."

mkdir -p out
find src -name "*.java" > sources.txt
javac --release 21 -d out @sources.txt || exit 1
rm -f sources.txt

echo "1. Все команды этапа 4 на дереве из четырёх уровней"
java -cp out Main --vfs vfs-samples/deep-tree --script scripts/stage4-all.txt

echo "2. Те же команды без VFS"
java -cp out Main --script scripts/stage4-all.txt

echo "Готово."
