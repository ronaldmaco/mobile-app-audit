I found the cause and fixed it. All 6 tests pass (`./gradlew test`). The 3 new ones failed before the fix.

## Why it happens

The favourites list is kept in memory (`FavoritesRepository`) so the Home tab loads fast. That copy is only cleared when the user taps **Log out**: `logout()` calls `onSignedOut`, which empties the favourites and recent searches.

The session can also end **on the server**: the password was changed on another device, or the token was revoked. The next API call then gets a 401 and `ApiClient` calls `SessionManager.sessionExpired()` (`session/SessionManager.kt:26`). That method cleared the token but never called `onSignedOut`. So:

1. Ana is signed in and the Home tab has loaded her favourites.
2. Her session expires on the server, and the app sends her to the login screen.
3. Ben signs in on the same device, and Home shows Ana's favourites. They stay until the app is restarted.

That's why it only happens sometimes: an explicit Log out clears everything, and only the expiry path leaks.

Two related gaps showed up on the same path:
- **Profile (drawer header / Account screen):** it was never cleared, not even on a normal Log out. The next user saw the previous person's name in every case.
- **Signing in while a session is still active** also kept the previous user's cached data.

## The fix (small, public API unchanged)

- `SessionManager.sessionExpired()` now calls `onSignedOut()`. It only does this when a session is actually active, so repeated 401s don't clear things twice.
- `SessionManager.login()` calls `onSignedOut()` first if it replaces a session that is still active.
- `ProfileRepository` gets a `clear()`, and `AppGraph` calls it alongside favourites and recent searches.

All per-user data is now dropped however a session ends.

## Tests added (`SessionTest.kt`)
- Session expires on the server, then a different user signs in: they see their own favourites, the recent searches are empty, and the profile is theirs.
- Log out, then a different user signs in: the profile is theirs.
- Signing in over an active session: the new user's favourites are shown.

## Still open
- **One timing case I couldn't test:** these classes are synchronous, so I couldn't reproduce it. In the real app, a favourites request started for user A could finish after user B has signed in and fill the cache with A's list. If the UI loads favourites in the background, the cache should only be filled when the session that made the request is still the current one.
- **Delivery:** the fix only reaches users once it's in a release build. Users on the current build will keep seeing this until they update. Nothing is committed yet; tell me if you want me to commit it.
