I found the cause and fixed it. All 7 tests pass. The 4 new ones fail on the old code and pass with the fix.

## Why it happens

The favourites list for the Home tab is kept in memory (`FavoritesRepository.cache`). It gets cleared only through `SessionManager.onSignedOut`, and only `logout()` calls that.

A session can also end because the server rejects the token, for example after a password change on another device or a revoked token. In that case `ApiClient` calls `SessionManager.sessionExpired()` (`session/SessionManager.kt:26`). That clears the token and sends the user to the login screen, but it never calls `onSignedOut()`. So the previous user's favourites stay in memory. When someone else signs in, `favorites()` returns that saved list straight away and never asks the server.

That's why it only happens sometimes. Pressing "Sign out" works fine; the bug only appears when the session expired on the server side before the account switch.

While mapping everything else that keeps per-user data, I found related leaks:
- **Profile (drawer and Account screen):** `ProfileRepository` had no way to clear its saved profile, so the previous person's name showed after any account switch, even a normal sign-out.
- **Recent searches:** these were also kept after a session expiry.
- **Late responses:** a favourites or profile request sent for user A that came back after user B had signed in was still saved and shown as B's data.

## The fix

The public API is unchanged and nothing on screen looks different.
- **`session/SessionManager.kt`:** `logout()` and `sessionExpired()` now both use one shared cleanup step that clears the token and calls `onSignedOut()`. A successful `login()` also clears per-user data before storing the new token, so nothing from a previous session can carry over.
- **`profile/ProfileRepository.kt`:** added `clear()`, which `AppGraph` now calls along with the favourites and searches cleanup.
- **`favorites/FavoritesRepository.kt` and `ProfileRepository.kt`:** each now keeps a counter that goes up every time it's cleared. A response that started before the last clear is thrown away instead of being saved or shown. In that rare case the screen briefly shows an empty list (or no profile) until the next load.

## Tests

The new file `android/src/test/kotlin/SessionSwitchTest.kt` covers:
- session expiry, then a different account, for favourites and profile
- sign-out, then a different account, for profile
- session expiry clearing recent searches
- a response from the previous session arriving after the new login

## Still open
- This was checked with unit tests on a simulated server only. I haven't run it on a device or a release build, so it isn't confirmed on the build users will get, and it isn't released.
- I only had the Android code. If there's an iOS app or another app sharing this login code, it should get the same check.
- Running the build left untracked `.gradle/` and `build/` folders because the repo has no `.gitignore`. They aren't part of the change.

Nothing is committed yet. Should I commit it?
