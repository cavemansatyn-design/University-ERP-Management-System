#!/usr/bin/env bash
# Run University ERP from project root. Invoke from repo root: ./scripts/run.sh
set -e
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT="$ROOT/out"
CP="$OUT:$ROOT/lib/*:$ROOT/resources"
SRC="$ROOT/src/main/java"

if [ ! -f "$ROOT/config/config.properties" ]; then
  echo "ERROR: config/config.properties not found."
  echo "Copy config/config.properties.example to config/config.properties and set your database credentials."
  exit 1
fi
if [ ! -d "$ROOT/lib" ]; then
  echo "ERROR: lib folder not found. Add JARs: flatlaf, jbcrypt, mysql-connector-j."
  exit 1
fi

echo "Compiling..."
mkdir -p "$OUT"
javac -encoding UTF-8 -d "$OUT" -cp "$ROOT/lib/*" -sourcepath "$SRC" "$SRC/edu/univ/erp/Main.java"
echo "Running..."
exec java -cp "$CP" edu.univ.erp.Main
