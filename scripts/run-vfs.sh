#!/usr/bin/env bash
cd "$(dirname "$0")/.."

mkdir -p out
find src -name "*.java" > sources.txt
javac --release 21 -d out @sources.txt || exit 1
rm -f sources.txt

echo "1. Минимальная VFS: один файл"
java -cp out Main --vfs vfs-samples/minimal --script scripts/stage3-all.txt

echo "2. Несколько файлов в корне"
java -cp out Main --vfs vfs-samples/several-files --script scripts/stage3-all.txt

echo "3. Дерево из четырёх уровней"
java -cp out Main --vfs vfs-samples/deep-tree --script scripts/stage3-all.txt

echo "4. Без VFS"
java -cp out Main --script scripts/stage3-all.txt

echo "5. Несуществующая директория VFS"
java -cp out Main --vfs vfs-samples/no-such-dir

echo "6. Источник VFS не директория, а файл"
java -cp out Main --vfs vfs-samples/minimal/readme.txt

echo "Готово."
