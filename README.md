# Chess Alarm

An Android alarm clock that won't shut up until you solve a chess puzzle. Set an alarm; when it fires, the app launches a mate-in-one position and won't dismiss the sound until you enter the correct move.

Built as a practice project to demonstrate Android fundamentals (Activities, BroadcastReceivers, the system AlarmManager, MediaPlayer) alongside five classic GoF design patterns.

---

## Design patterns

| Pattern | Where | Why |
|---|---|---|
| **Factory** | `PuzzleFactory` | Centralizes puzzle creation so the data source can swap (hardcoded → API → DB) without touching callers. |
| **Strategy** | `PuzzleSolver` interface, `MateInOneSolver` impl | Different puzzle types will need different validation logic — solver swaps in without changing `PuzzleActivity`. |
| **Singleton** | `AlarmController` | One source of truth for alarm state shared across `MainActivity`, `PuzzleActivity`, and `AlarmReceiver`. |
| **Observer** | `AlarmStateListener` | Decouples alarm state changes from UI updates; `PuzzleActivity` just registers and reacts. |
| **Command** | `Command` / `StartAlarmCommand` / `StopAlarmCommand` | Encapsulates alarm actions so they're testable and could later be queued, logged, or undone. |

`AlarmController` also takes its `SoundPlayer` via constructor injection, keeping the state/observer logic JVM-testable and the Android `MediaPlayer` plumbing isolated to `RingtoneSoundPlayer`.

---

## Architecture

```
MainActivity ──[set time]──► Android AlarmManager ──[at time]──► AlarmReceiver
                                                                       │
                                                                       ▼
                                                                AlarmController ──notifies──► AlarmStateListener
                                                                       │                            ▲
                                                                       ▼                            │
                                                                StartAlarmCommand            PuzzleActivity
                                                                       │                  (registers, dismisses
                                                                       ▼                   on correct move)
                                                                  SoundPlayer
                                                                (Ringtone impl)
```

Package layout:

```
com.example.chessalarm
├── MainActivity              // set alarm time, schedule with system AlarmManager
├── PuzzleActivity            // shows puzzle, validates move, dismisses on correct answer
├── alarm/
│   ├── AlarmController       // singleton: state + listeners
│   ├── AlarmStateListener    // observer interface
│   ├── AlarmReceiver         // BroadcastReceiver triggered by system AlarmManager
│   ├── Command               // command interface
│   ├── StartAlarmCommand
│   ├── StopAlarmCommand
│   └── RingtoneSoundPlayer   // Android MediaPlayer impl of AlarmController.SoundPlayer
└── puzzle/
    ├── ChessPuzzle           // immutable model (FEN, correct move, difficulty)
    ├── PuzzleSolver          // strategy interface
    ├── MateInOneSolver       // normalized SAN comparison
    └── PuzzleFactory         // hardcoded puzzle source
```

---

## Build and run

**Requirements:**
- Android Studio (Hedgehog or later)
- JDK 11+
- An emulator (AVD) or physical device running API 24 (Android 7.0) or later

**From the command line:**
```bash
./gradlew assembleDebug   # build the APK
./gradlew test            # run unit tests
./gradlew installDebug    # install onto a connected device/emulator
```

**From Android Studio:** open the project, pick a device from the toolbar dropdown, hit Run.

---

## Permissions

Declared in `AndroidManifest.xml`:

| Permission | Why |
|---|---|
| `SCHEDULE_EXACT_ALARM` | Required on API 31–32 to use exact alarms; user must grant via Settings on first run. |
| `USE_EXACT_ALARM` | Equivalent grant on API 33+ for alarm-clock-style apps. |
| `WAKE_LOCK` | Lets the alarm wake the device. |
| `RECEIVE_BOOT_COMPLETED` | Reserved for re-scheduling alarms after reboot (not yet wired). |

On API 31–32, the first tap of "Set Alarm" routes the user to the system Settings screen to grant `SCHEDULE_EXACT_ALARM`. After flipping the toggle, return to the app and tap Set Alarm again.

---

## Testing

**Unit tests** live in `app/src/test/` and run on the JVM (no emulator needed):

- `MateInOneSolverTest` — covers correct/wrong/null/empty/case/whitespace cases on the strategy
- `AlarmControllerTest` — covers state transitions, listener add/remove/duplicate, idempotent trigger/dismiss, and command-driven sound calls (using a fake `SoundPlayer`)

```bash
./gradlew test
```

**Instrumented tests** (Espresso) are scaffolded in `app/src/androidTest/` for future UI coverage.

---

## Known limitations / next steps

- **Puzzle pool is small.** `PuzzleFactory` ships with two verified mate-in-ones (Fool's mate, back-rank mate). More verified positions can be added from [lichess.org/training/mateIn1](https://lichess.org/training/mateIn1).
- **Move input is plain text in SAN.** A tap-based chessboard UI is a natural stretch goal.
- **Full-screen launch from background.** On API 29+, starting `PuzzleActivity` from a `BroadcastReceiver` while the device is unlocked and in another app may be silently denied. A `USE_FULL_SCREEN_INTENT` notification is the proper fix.
- **Reboot persistence.** Alarms are not yet re-scheduled after a device reboot; the `RECEIVE_BOOT_COMPLETED` permission is declared but no `BootReceiver` is wired.
- **`onBackPressed` is deprecated** as of API 33. Should migrate to `OnBackPressedDispatcher`.

---

## License

Personal/educational project. No license attached.
