# Inventory and static checklist

Read the code path end to end for each area in scope and answer every question. Every "no" or "unclear" becomes a finding with file:line and a concrete trigger, and gets an impact map (SKILL.md 2.4).

## Inventory (modes C, D)
- Stack, minimum/target OS and SDK, build system, dependency manager, environments and base URLs (xcconfig, Info.plist, build.gradle, AndroidManifest, .env, pubspec.yaml, app.json/app.config, eas.json).
- Every screen and flow (storyboards, navigation graphs, routers), every endpoint (host, method, auth, caching), background work (push, deep links, timers), local persistence (keychain/keystore, user defaults/shared prefs, databases, caches), third-party SDKs, feature flags, remote config.
- **Shared data map:** for each piece of user data (preferences, filters, likes, profile), which screens read it and which requests send it. This is the "consumers" axis of every impact map.
- Test setup: which test targets exist and whether they build and run today.
- Ask only for what cannot be found: test accounts, devices/regions that matter, known complaints (recorded as hypotheses).

## Launch, lifecycle, navigation
- Correct lifecycle for the target SDK (iOS UIScene; Android activity/fragment lifecycle, process death; Flutter/RN app state).
- Root replacement does not stack screens; no duplicate presentations; back never lands on a screen that should be gone.
- Deep links and push work cold and warm; pending actions wait until the UI is ready.
- No force unwraps / `!!` / unchecked casts on data that can be missing.
- Callbacks that pop or dismiss check the screen is still on top.
- System notifications can arrive unpaired (keyboard hide without show, background without foreground); handlers do not assume the pair.
- After a lifecycle migration (e.g. UIScene), every URL entry point (deep links, social login callbacks, push) is retested cold and warm.

## Authentication and session
- Login, sign up, forgot and reset show the server's error in friendly words; success never shown on an error body.
- Public auth routes do not send a stale token.
- Session survives kill, reboot and locked device (keychain after first unlock; Android keystore).
- 401 logs out once, only for the app's own API host; token refresh is single-flight.
- Logout clears token, cached user, per-user caches and analytics identity.
- In-app account deletion exists when sign up exists, waits for the server and confirms.

## Networking
- Every request has a timeout; every failure reaches the UI (offline, timeout, 4xx, 5xx, HTML/non-JSON).
- Caching: what is cached, keyed by what, when used. Writes and per-user reads never served stale as fresh.
- A completion can fire more than once? Only the first answer counts.
- Lenient parsing where the server can add or null fields.
- UI and shared state only on the main thread.
- No secrets in logs, URLs or query strings; HTTPS only.
- Every read uses a read method (GET); no write method used to fetch data.
- A non-2xx status is an error even when the body decodes into the model.
- **Request parameters:** what does an empty array, null, 0 or a missing key mean to the server? Never send "empty" when the user asked for something specific.

## Data, state, persistence
- Single source of truth for the current user and settings; local copy updated from the server response after writes.
- Refresh-on-appear cannot overwrite a newer value from a save in flight.
- Models compared by stable id, not full equality; if ids are unreliable (0, duplicates), by normalised name.
- Sentinel values ("none", 0, -1) never share a representation with real data.
- Hard-coded lists match the server.
- Stored data from older app versions decodes or migrates.

## Screens with actions (forms, pickers, toggles, likes, purchases)
- Loading shown, repeat taps disabled, waits for the server, then closes/updates.
- On failure: friendly message, input kept, retry possible, optimistic UI reverted.
- Validation matches the server.
- Toggles/likes match server state after restart and on other screens.
- Purchases: receipt validation, restore, pending/deferred states, entitlement refresh.

## Lists, search, filters, pagination
- Loading, empty, error and end-of-list states exist and are distinct.
- Refresh and pagination do not duplicate or skip; stale requests ignored when filters change.
- Every selected filter shows as selected **everywhere** (chips, filter sheet, settings) and is applied to the request; none is dropped silently.
- The result for each selected filter matches the API for the same parameters (oracle).
- A filter that can match nothing produces an honest empty state, not unfiltered results.
- Search debounced, cancels the previous request, handles no results.

## Media and web content
- Images: placeholders, disk caching (shows offline), failure fallback, no main-thread decoding; a late image for a reused cell is dropped.
- Video/web views on current APIs, handle no network and autoplay.
- External links open in the right place, return to the app, and do not 404.

## Push and background
- Permission flow, token sent to the backend and refreshed, cleared on logout.
- APNs/FCM config valid, not expiring, right environment.
- Notification taps route correctly cold/warm.

## Layout and device variety
- Small/large phones, tablets, rotation, dark mode, Dynamic Type, RTL if localized.
- Safe areas, keyboard covering inputs.
- No layout on deprecated/no-op APIs.

## Platform and dependencies
- Target/minimum SDK meet store requirements; deprecation warnings reviewed.
- SDKs initialised once, on the right thread, versions supporting the current OS.
- Known vulnerabilities listed, not upgraded without approval.

## Analytics and logging
- Events fire once per action, after success, no personal data beyond policy.
- Debug logging reduced in release builds.

## Security (OWASP MASVS based)
- **Storage:** tokens and secrets only in Keychain/Keystore; no sensitive data in logs, app switcher snapshots, backups, clipboard.
- **Secrets in the bundle:** grep source and built app (`strings`, unzip IPA/APK). Server secrets are Critical.
- **Network:** TLS only, no disabled certificate validation; pinning where warranted.
- **Auth:** server enforces authorization per object (IDOR with a test account); rate limits; reset tokens expire.
- **Platform:** exported components and deep links validate input; WebView bridges limited.
- **Code:** release builds obfuscated where expected; debug flags off.
- **Privacy:** permissions only when needed, purpose strings; data matches privacy manifest / Data safety.
- **Consent:** every consent switch (analytics, ads, personalisation) is wired to every SDK it governs, reverts on a failed save, and logout resets SDK identities.
- **Caches:** per-user caches live in a non-backed-up location (iOS `Caches`, Android `cacheDir`), never store write responses, and are cleared on logout.

## Performance
- Cold start well under 2 s to first useful screen (Instruments App Launch; `adb shell am start -W`). First launch after install can be slower; measure a normal relaunch too.
- Main-thread work and jank (Time Profiler, Android Profiler, Flutter DevTools).
- Memory growth on repeated navigation (Leaks, LeakCanary).
- ANRs / watchdog kills in production reports.

## Accessibility
- Every tappable element labelled; touch targets 44pt / 48dp; WCAG AA contrast; largest font without clipping.
- Logical focus order; no information by colour only; reduce motion respected.

## Localization
- No hard-coded user strings if localized; locale formats; long translations and RTL fit.

## Stack notes
- **iOS (UIKit/SwiftUI):** outlets, main-thread UI, `[weak self]`, Keychain accessibility class, scene URL handling, `Task` cancellation. spaces and symbols in the product name become underscores in the Swift module name; test targets need `TEST_HOST` matching the real product name.
- **Android:** lifecycle-aware coroutines, process death (`SavedStateHandle`), no network on main, R8 keep rules, cleartext config, exported components.
- **Flutter:** `mounted` after `await`, `setState` after dispose, platform channel errors, release-only issues.
- **React Native / Expo:** unhandled rejections, effect cleanup, Hermes/release differences, Expo runtime version vs OTA channel, secrets in `app.config`.
