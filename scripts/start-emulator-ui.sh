#!/usr/bin/env bash
# Start SEDI on the Android emulator with a visible UI.
# Uses headless emulator + scrcpy (stable on Apple Silicon). Native emulator
# window may close when launched from IDE automation shells.

set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
AVD="Medium_Phone_API_35"
PKG="gov.utah.sedi"
APK="$ROOT/app/build/outputs/apk/debug/app-debug.apk"
EMU="${ANDROID_HOME:-$HOME/Library/Android/sdk}/emulator/emulator"
ADB="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools/adb"
LOG="/tmp/sedi-emulator-run.log"

export PATH="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools:${ANDROID_HOME:-$HOME/Library/Android/sdk}/emulator:$PATH"

if ! command -v scrcpy >/dev/null; then
  echo "scrcpy not found. Install with: brew install scrcpy"
  exit 1
fi

if ! "$ADB" devices | grep -qE 'emulator-[0-9]+\s+device'; then
  echo "Starting emulator (headless)..."
  nohup "$EMU" -avd "$AVD" \
    -no-window -no-audio \
    -gpu swiftshader_indirect \
    -no-snapshot-load -no-snapshot-save \
    >"$LOG" 2>&1 &
  echo "Waiting for boot..."
  "$ADB" wait-for-device
  while [ "$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r\n')" != "1" ]; do
    sleep 2
  done
fi

echo "Building and installing..."
(cd "$ROOT" && gradle installDebug -q)

echo "Launching app..."
"$ADB" shell am start -n "$PKG/.MainActivity"

echo "Opening screen mirror (scrcpy). Close scrcpy to stop mirroring; emulator keeps running."
exec scrcpy --stay-awake --window-title "Utah Identity Wallet"
