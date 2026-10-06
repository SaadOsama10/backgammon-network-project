#!/bin/sh
# Compiles the project and runs the automated game tests (they open real Swing windows, so a display is needed).
#   ./run-tests.sh          # TCP game + local game
#   ./run-tests.sh Tcp      # only TcpGameTest   (or: Local)
set -e
cd "$(dirname "$0")"
OUT=build/test-classes
rm -rf "$OUT" && mkdir -p "$OUT"
javac --release 17 -cp lib/AbsoluteLayout.jar -d "$OUT" $(find src test -name '*.java')
cp -R src/images "$OUT/"
cp src/client/*.png "$OUT/client/"
run() { java -cp "$OUT:lib/AbsoluteLayout.jar" "$1"; }
case "${1:-all}" in
  Tcp)   run client.TcpGameTest ;;
  Local) run client.LocalModeTest ;;
  *)     run client.TcpGameTest; run client.LocalModeTest ;;
esac
