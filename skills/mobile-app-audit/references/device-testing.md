# Device testing

## Environment preflight (before the first build)
- Free disk: `df -h <work folder>`; a build plus tests needs ~10 GB. Below that, ask the user to free space before starting (an ENOSPC mid-session stops every command).
- One derived data / build folder per session (e.g. `-derivedDataPath <work folder>/dd`), reused for builds and tests; delete it when the work is merged.
- Test target builds today? If not, say so before promising tests.
- Latest OS simulator/emulator available, plus the minimum supported OS if the bug may be OS-specific.

## Tools: use what the agent has
The shell works in every agent and is the default. Use a device tool when the agent has one: it adds element lists and taps without coordinates guesswork. Tool names differ per agent; match by what the tool does.

| Need | Shell (any agent) | mobile-mcp (any MCP client) | Agent-specific simulator tools (e.g. Claude Code iOS Simulator) |
|---|---|---|---|
| Live view for the user | `open -a Simulator`; emulator window | n/a | attach / open panel |
| Install + launch | `xcrun simctl install booted <app>` + `xcrun simctl launch booted <bundle>`; `adb install`, `adb shell am start -n <pkg>/<activity>` | `mobile_install_app`, `mobile_launch_app` | launch with the `.app` path |
| See the screen | `xcrun simctl io booted screenshot f.png`; `adb exec-out screencap -p > f.png`; Android elements: `adb shell uiautomator dump /dev/tty` | `mobile_list_elements_on_screen` (preferred), `mobile_take_screenshot` | screenshot (small scale) |
| Act | `adb shell input tap/text/swipe`; iOS has no shell tap: use Maestro or a device tool | `mobile_click_on_screen_at_coordinates`, `mobile_swipe_on_screen`, `mobile_type_keys` | tap, swipe, type, key, button |
| Read stored state | iOS: `plutil -p "$(xcrun simctl get_app_container booted <bundle> data)/Library/Preferences/<bundle>.plist"`; Android: `adb shell run-as <pkg> cat shared_prefs/<file>.xml` | n/a | shell |
| Deep links | `xcrun simctl openurl booted <url>`; `adb shell am start -a android.intent.action.VIEW -d <url>` | `mobile_open_url` | open URL |
| Push (simulated) | `xcrun simctl push booted <bundle> payload.apns`; Android: send a test message from the FCM console or backend | n/a | shell |
| Dark mode, rotation | `xcrun simctl ui booted appearance dark`; `adb shell cmd uimode night yes`; `adb shell settings put system user_rotation 1` | `mobile_set_orientation` | appearance, rotate |
| Fresh state | `xcrun simctl uninstall booted <bundle>`; `adb shell pm clear <pkg>` | `mobile_clear_app_data` | reinstall |
| Logs, crashes | `xcrun simctl spawn booted log show --last 2m --predicate 'process == "<App>"'`; `adb logcat --pid=$(adb shell pidof <pkg>)` | `mobile_get_device_logs`, `mobile_list_crashes` | shell |
| Video evidence | `xcrun simctl io booted recordVideo out.mp4`; `adb shell screenrecord /sdcard/out.mp4` | `mobile_start/stop_screen_recording` | shell |
| Repeatable flows | `maestro test flows/` | Maestro | Maestro |

If the agent has no way to tap on iOS, run the flow with Maestro or ask the user to perform the steps while you read logs and stored state.

Build with the project's tooling; test the **release** configuration before shipping. If the build cannot run here (signing), ask the user to build and give the product path.

## Loop for every step
1. Look (element list if available, else a small screenshot).
2. Act on the centre of the element.
3. Verify the expected change before the next step; never act blind.
4. Record: case id, step, expected, actual, evidence file.

## Pitfalls that produce false results
- **Taps during scroll momentum or transitions land on the wrong row.** After a swipe, take a screenshot before tapping; use slow swipes (`duration` >= 1.5 s).
- **Taps right after a tab/screen change can be lost or hit the previous screen.** One action, then look.
- **Confirm saved state from storage or the server**, not only from the screen (e.g. read the stored user after "Done").
- `simctl launch` may start the app in the background; open it from its icon or the tool's `launch`.
- The first launch after a reinstall can show a long blank screen; judge launch time on a normal relaunch.
- Stray taps can open external pages; note them, close them, and do not report them as app bugs without reproducing.

## Conditions for critical flows
| Condition | How |
|---|---|
| Slow network | Network Link Conditioner; emulator `-netdelay`/`-netspeed` |
| Network lost mid-action | Wi-Fi off right after tapping; `adb shell svc wifi disable` |
| Other region | VPN on host; confirm with ipinfo |
| App killed and relaunched | terminate, launch |
| Fresh install / upgrade | uninstall, or install old build then new |
| Locked device at launch | real device, for keychain flows |
| Dark mode, large text, rotation, tablet | appearance, font scale, rotate, iPad |
| Oldest supported OS | simulator/emulator with the minimum OS |
| Background then resume after minutes | token expiry, sockets |

## Minimum scenario catalogue
- **Access:** sign up, log in (valid, wrong password, unconfirmed), forgot/reset (cold and warm link), log out, delete account, session survives kill and reboot.
- **Every write action:** persists after tab switch, kill, logout/login; offline failure keeps input; double tap saves once.
- **Every list/filter:** first load, refresh, paginate to the end, empty, offline, each filter shown as selected on every screen, count equals the oracle, search with/without results.
- **Every detail screen:** from list, deep link and push; media plays or fails gracefully.
- **Settings/account:** each row opens the right place, external links return, values match the server.
- **Purchases:** buy, cancel, restore, expired subscription.

Save evidence in the work folder with case ids in file names.
