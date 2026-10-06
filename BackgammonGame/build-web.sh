#!/bin/sh
# Builds ../docs/play/backgammon.jar for the browser demo (CheerpJ runs Java 8/11/17, so compile with --release 11).
# AbsoluteLayout is merged into the jar so one file is enough for cheerpjRunJar.
set -e
cd "$(dirname "$0")"
OUT=build/web
rm -rf "$OUT" && mkdir -p "$OUT" ../docs/play
javac --release 11 -cp lib/AbsoluteLayout.jar -d "$OUT" $(find src -name '*.java')
cp -R src/images "$OUT/"
cp src/client/*.png "$OUT/client/"
(cd "$OUT" && jar xf ../../lib/AbsoluteLayout.jar org && rm -rf META-INF)
printf 'Main-Class: client.GameWindow\n' > "$OUT/MANIFEST.MF"
jar cfm ../docs/play/backgammon.jar "$OUT/MANIFEST.MF" -C "$OUT" client -C "$OUT" game -C "$OUT" server -C "$OUT" images -C "$OUT" org
echo "built docs/play/backgammon.jar"
