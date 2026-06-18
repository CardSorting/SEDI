#!/usr/bin/env bash
# Run the Utah Identity Wallet demo on the Android emulator with a visible UI.
#
# One command does everything:
#   1. Starts a stable headless emulator (if needed)
#   2. Builds and installs the debug APK
#   3. Launches the app
#   4. Opens scrcpy so you see the phone screen on your desktop
#
# Usage:
#   bash scripts/start-emulator-ui.sh          # normal run
#   bash scripts/start-emulator-ui.sh --fresh  # uninstall first (reset demo data)
#
# Run from Terminal.app or double-click scripts/start-emulator-ui.command in Finder.
# Do not rely on IDE-embedded shells to start the native emulator window — they
# often exit immediately on macOS. This script is the supported path.

set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
AVD="Medium_Phone_API_35"
PKG="gov.utah.sedi"
EMU="${ANDROID_HOME:-$HOME/Library/Android/sdk}/emulator/emulator"
ADB="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools/adb"
LOG="/tmp/sedi-emulator-run.log"
FRESH=false

for arg in "$@"; do
  case "$arg" in
    --fresh) FRESH=true ;;
    -h|--help)
      sed -n '2,16p' "$0"
      exit 0
      ;;
  esac
done

export PATH="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools:${ANDROID_HOME:-$HOME/Library/Android/sdk}/emulator:$PATH"

if ! command -v scrcpy >/dev/null; then
  echo "scrcpy is required for the on-screen demo window."
  echo "Install once: brew install scrcpy"
  exit 1
fi

if ! command -v gradle >/dev/null; then
  echo "gradle not found on PATH."
  exit 1
fi

echo "==> Utah Identity Wallet — demo launcher"
echo ""

if "$ADB" devices 2>/dev/null | grep -qE 'emulator-[0-9]+\s+device'; then
  echo "==> Emulator already running"
else
  echo "==> Starting emulator (headless, stable on Apple Silicon)..."
  nohup "$EMU" -avd "$AVD" \
    -no-window -no-audio \
    -gpu swiftshader_indirect \
    -no-snapshot-load -no-snapshot-save \
    >"$LOG" 2>&1 &
  echo "    Log: $LOG"
  echo "==> Waiting for boot (up to ~2 min on cold start)..."
  "$ADB" wait-for-device
  for _ in $(seq 1 60); do
    boot="$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r\n')"
    if [ "$boot" = "1" ]; then
      break
    fi
    sleep 2
  done
  if [ "$boot" != "1" ]; then
    echo "Emulator did not finish booting. Check: tail -30 $LOG"
    exit 1
  fi
  echo "==> Emulator ready"
fi

if [ "$FRESH" = true ]; then
  echo "==> Removing previous install (fresh demo)..."
  "$ADB" uninstall "$PKG" 2>/dev/null || true
fi

echo "==> Building and installing app..."
(cd "$ROOT" && gradle installDebug -q)

echo "==> Launching app..."
"$ADB" shell am start -n "$PKG/.MainActivity"

echo ""
echo "==> Opening screen mirror (scrcpy)"
echo "    Look for a window titled \"Utah Identity Wallet\"."
echo "    Closing scrcpy stops mirroring only — the emulator keeps running."
echo "    Stop emulator later: adb emu kill"
echo ""

exec scrcpy --stay-awake --window-title "Utah Identity Wallet"
