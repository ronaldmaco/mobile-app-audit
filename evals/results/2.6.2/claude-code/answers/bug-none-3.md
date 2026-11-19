The previous user's favourites stayed in memory whenever their session ended without them signing out. I've fixed it, and the tests pass, including two new ones that failed before the fix.

**Why it happened:** Favourites are cached in memory (`FavoritesRepository`) so the Home tab loads fast. The cache is only cleared through `SessionManager.onSignedOut`, and only an explicit `logout()` triggered that. Sessions can also end because the server rejects the token: a password change on another device, a revoked token, or plain expiry. In that case `ApiClient` calls `sessionExpired()`, which removed the token but never cleared the cache. The app returns to the login screen, the next person signs in, and Home serves the old cached list. That's why it only happens sometimes: users who tapped "Log out" were fine, and those whose session timed out were not.

**Fix:**
- **`SessionManager.sessionExpired()`** now clears per-user data, just like `logout()`.
- **`SessionManager.login()`** also clears per-user data before storing the new token, so nothing from an earlier session can carry over into a new one.
- **Profile:** while checking this I found the same bug in a second place. The profile in the drawer and Account screen (`ProfileRepository`) was never cleared, not even on a normal logout, so it would show the previous person's name. It now has a `clear()`, which `AppGraph` calls along with clearing favourites and recent searches.

The public API is unchanged.

**New tests** in `SessionTest.kt`:
- Ana's token is revoked on the server and Ben signs in. Ben must see his own favourites and profile, and no recent searches.
- After a normal logout and a new login, the profile belongs to the new user.

**Still possible:** if a favourites request is still in progress when the account switches, its late response could put the old user's data back in the cache. The code here runs synchronously, so this can't happen now. If these calls become asynchronous in the app, the cache should check that the session is still the one that started the request before storing the result.
