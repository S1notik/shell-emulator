#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
mkdir -p out out-test
find src -name "*.java" > sources.txt
javac --release 21 -d out @sources.txt
rm -f sources.txt
find tests -name "*.java" > test-sources.txt
javac --release 21 -cp out -d out-test @test-sources.txt
rm -f test-sources.txt
java -cp out:out-test parser.CommandParserTest
