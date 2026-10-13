#!/usr/bin/env bash
cd "$(dirname "$0")/.."

mkdir -p out
find src -name "*.java" > sources.txt
javac --release 21 -d out @sources.txt || exit 1
rm -f sources.txt

echo "1. Без параметров"
java -cp out Main

echo "2. Только путь к VFS"
java -cp out Main --vfs vfs-samples/minimal

echo "3. Только стартовый скрипт"
java -cp out Main --script scripts/stage2-basic.txt

echo "4. Оба параметра"
java -cp out Main --vfs vfs-samples/minimal --script scripts/stage2-basic.txt

echo "5. Скрипт с ошибочными строками"
java -cp out Main --script scripts/stage2-errors.txt

echo "6. Несуществующий скрипт"
java -cp out Main --script scripts/no-such-file.txt

echo "7. Неизвестный параметр"
java -cp out Main --wrong value

echo "8. Параметр без значения"
java -cp out Main --vfs

echo "Готово."
