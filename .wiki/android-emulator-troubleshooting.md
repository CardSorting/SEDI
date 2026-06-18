# Android Emulator Troubleshooting

Guide for building, installing, and running **Utah Identity Wallet** (`gov.utah.sedi`) on the Android emulator during local development.

## Prerequisites

- Android SDK at `$ANDROID_HOME` (default: `~/Library/Android/sdk`)
- `adb` and `emulator` on your `PATH`
- Gradle available as `gradle` (this repo does not ship a `gradlew` wrapper)
- AVD: **Medium Phone API 35** (`Medium_Phone_API_35`)

Check setup:

```bash
echo $ANDROID_HOME
adb devices
emulator -list-avds
```

## Quick start (recommended)

With a device or emulator connected and booted:

```bash
cd /path/to/SEDI
gradle assembleDebug
gradle installDebug
adb shell am start -n gov.utah.sedi/.MainActivity
```

`gradle installDebug` builds if needed, installs the APK, and is safer than launching before install.

## Fresh install (clear old app data)

If the emulator shows stale demo data or an outdated UI, uninstall before reinstalling:

```bash
gradle clean assembleDebug
adb uninstall gov.utah.sedi
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n gov.utah.sedi/.MainActivity
```

`-r` replaces an existing install; `adb uninstall` fully removes app storage.

## Common errors

### `Error type 3` — Activity class does not exist

```
Error: Activity class {gov.utah.sedi/gov.utah.sedi.MainActivity} does not exist.
```

**Cause:** The app is not installed on the connected device/emulator. `am start` was run before `adb install`.

**Fix:**

```bash
adb devices                    # confirm a device is listed as "device"
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell pm path gov.utah.sedi   # should print package:/data/app/...
adb shell am start -n gov.utah.sedi/.MainActivity
```

Or use `gradle installDebug` instead of manual install.

### No devices attached

```
List of devices attached
(empty)
```

**Cause:** Emulator is not running, or adb lost connection.

**Fix:**

1. Start the AVD from **Android Studio → Device Manager**, or
2. Start headless from the terminal (stable on Apple Silicon):

```bash
emulator -avd Medium_Phone_API_35 \
  -no-window -no-audio \
  -gpu swiftshader_indirect \
  -no-snapshot-load -no-snapshot-save &
```

Wait for boot:

```bash
adb wait-for-device
adb shell 'while [ "$(getprop sys.boot_completed)" != 1 ]; do sleep 2; done'
```

### Emulator crashes on startup

**Symptoms:** Emulator window closes immediately, or log stops at `Created extended window`.

**Common causes:**

| Cause | What you see | Fix |
|-------|----------------|-----|
| GPU / snapshot mismatch | `Failed to load snapshot 'default_boot'`, `different renderer configured` | Use consistent `-gpu` flags; see stable launch below |
| Stale AVD lock | Emulator exits seconds after start | Remove locks: `rm -rf ~/Library/Caches/TemporaryItems/avd/running/*` |
| Background start from terminal | Process dies when shell exits | Keep emulator in foreground, use Android Studio, or use `nohup` / long-running session |

**Stable CLI launch (headless, software GPU):**

```bash
emulator -avd Medium_Phone_API_35 \
  -no-window -no-audio \
  -gpu swiftshader_indirect \
  -no-snapshot-load -no-snapshot-save
```

**GUI emulator from terminal** may crash on some macOS setups — the window can open and immediately close (log stops after `Created extended window`). This is common when launched from IDE automation shells.

**Reliable visible UI (recommended):**

```bash
# From your own Terminal.app (not IDE shell):
cd /path/to/SEDI
bash scripts/start-emulator-ui.sh
```

This script starts a **headless** emulator (stable), installs the app, and opens **scrcpy** to mirror the screen in a desktop window. Requires `brew install scrcpy`.

Alternatives:
- **Android Studio → Device Manager** → play button on the AVD
- Headless emulator + `scrcpy --stay-awake --window-title "Utah Identity Wallet"`

**Cold boot** (if snapshots stay corrupted):

- Android Studio → Device Manager → ⋮ on the AVD → **Cold Boot Now**
- Or delete snapshot: `rm -rf ~/.android/avd/Medium_Phone_API_35.avd/snapshots/default_boot`

### App shows old data after rebuild

**Cause:** Previous install persisted in emulator storage. SEDI uses in-memory demo data seeded at app start, but an old APK may still be installed.

**Fix:** Uninstall, reinstall, and relaunch (see [Fresh install](#fresh-install-clear-old-app-data)).

To reset the entire emulator:

- Device Manager → ⋮ → **Wipe Data**

### Build succeeds but UI unchanged

1. Confirm install targeted the running emulator: `adb devices`
2. Confirm package version: `adb shell dumpsys package gov.utah.sedi | grep versionName`
3. Force-stop and relaunch:

```bash
adb shell am force-stop gov.utah.sedi
adb shell am start -n gov.utah.sedi/.MainActivity
```

## Useful diagnostic commands

```bash
# Connected devices
adb devices -l

# Is the app installed?
adb shell pm list packages | grep sedi

# Resolve launcher activity
adb shell cmd package resolve-activity --brief gov.utah.sedi

# Install log
adb install -r -d app/build/outputs/apk/debug/app-debug.apk

# Emulator log (when started with redirect)
tail -f /tmp/sedi-emulator-run.log

# Kill emulator
adb emu kill
```

## APK location

After `gradle assembleDebug`:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Package reference

| Item | Value |
|------|-------|
| Application ID | `gov.utah.sedi` |
| Launcher activity | `gov.utah.sedi.MainActivity` |
| Start intent | `adb shell am start -n gov.utah.sedi/.MainActivity` |

## Known non-blocking build notices

- `Unable to strip ... libandroidx.graphics.path.so` — packaging warning only; debug builds still succeed.
- Gradle deprecated-feature warnings — do not block `assembleDebug`.
