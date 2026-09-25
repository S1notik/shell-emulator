#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
mkdir -p out
find src -name "*.java" > sources.txt
javac --release 21 -d out @sources.txt
rm -f sources.txt
java -cp out Main
