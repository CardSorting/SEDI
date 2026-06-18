# Run the Demo

**One command** starts the emulator, installs the app, launches it, and opens a desktop window so you can use the demo.

```bash
cd /path/to/SEDI
bash scripts/start-emulator-ui.sh
```

Reset in-memory demo data (uninstall + reinstall):

```bash
bash scripts/start-emulator-ui.sh --fresh
```

### macOS: double-click

In Finder, open `scripts/` and double-click **`start-emulator-ui.command`**. Terminal opens and runs the same workflow automatically.

### What happens automatically

| Step | Action |
|------|--------|
| 1 | Starts **Medium Phone API 35** headless if no emulator is connected (stable on Apple Silicon) |
| 2 | Waits for Android to finish booting |
| 3 | Runs `gradle installDebug` (builds if needed) |
| 4 | Launches **Utah Identity Wallet** |
| 5 | Opens **scrcpy** — a mirror window titled `Utah Identity Wallet` |

You do **not** need to run `adb install`, `am start`, or start the emulator manually when using this script.

### First-time setup

One-time prerequisites:

```bash
# Android SDK (Android Studio installs this)
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"

# Screen mirror (required for visible UI)
brew install scrcpy

# Gradle on PATH (no gradlew in this repo)
# Verify:
adb devices
emulator -list-avds    # should list Medium_Phone_API_35
gradle -v
```

Create the AVD in **Android Studio → Device Manager** if `Medium_Phone_API_35` is missing (API 35, medium phone).

---

## Why this script exists

Direct emulator launches from **IDE automation shells** (including Cursor agents) are unreliable on macOS:

| Approach | Result |
|----------|--------|
| `setsid emulator …` | Fails — `setsid` is not available on macOS |
| `nohup emulator …` (GUI, from IDE shell) | Window often opens then **closes immediately** |
| **`bash scripts/start-emulator-ui.sh`** (from **Terminal.app**) | **Works** — headless emulator + scrcpy mirror |
| Android Studio → Device Manager → Play | Works for native emulator window |

The script uses a **headless emulator** (stable) plus **scrcpy** (visible desktop window). That is the supported demo path.

**Going forward:** always run `bash scripts/start-emulator-ui.sh` from Terminal, or double-click `start-emulator-ui.command`. For a native emulator window instead of scrcpy, use Android Studio’s Device Manager.

---

## Manual commands (optional)

Use these only if you already have a device connected and booted:

```bash
gradle installDebug
adb shell am start -n gov.utah.sedi/.MainActivity
```

Fresh install without the script:

```bash
gradle clean installDebug
adb uninstall gov.utah.sedi
gradle installDebug
adb shell am start -n gov.utah.sedi/.MainActivity
```

---

## Troubleshooting

### `Error type 3` — Activity class does not exist

The app is not installed. Run the launcher script, or `gradle installDebug` before `am start`.

### No devices attached

Run `bash scripts/start-emulator-ui.sh`, or start the AVD from Android Studio.

### Emulator crashes on startup

- Clear stale locks: `rm -rf ~/Library/Caches/TemporaryItems/avd/running/*`
- Cold boot: Device Manager → ⋮ → **Cold Boot Now**
- Check log: `tail -30 /tmp/sedi-emulator-run.log`

### Native emulator window closes immediately

Expected when started from an IDE shell. Use **`start-emulator-ui.sh`** or Android Studio instead.

### App shows old demo data

```bash
bash scripts/start-emulator-ui.sh --fresh
```

### scrcpy window behind other apps

Check the Dock or Mission Control for **Utah Identity Wallet**.

### Stop the emulator

```bash
adb emu kill
```

---

## Reference

| Item | Value |
|------|-------|
| Launcher script | `scripts/start-emulator-ui.sh` |
| macOS double-click | `scripts/start-emulator-ui.command` |
| Application ID | `gov.utah.sedi` |
| Launcher activity | `gov.utah.sedi/.MainActivity` |
| AVD | `Medium_Phone_API_35` |
| APK | `app/build/outputs/apk/debug/app-debug.apk` |
| Emulator log | `/tmp/sedi-emulator-run.log` |
