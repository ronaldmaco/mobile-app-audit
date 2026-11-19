I found the cause and fixed it. Both new tests fail on the old code and pass with the fix, and all 5 tests pass now.

## Why it happens
There are two ways a user gets signed out, and only one of them was cleaning up:

- **Tapping Log out** (`SessionManager.logout()`) clears the token and runs `onSignedOut`. That callback empties the favourites cache and recent searches.
- **The server ending the session** (`SessionManager.sessionExpired()`) happens when the server rejects the token, for example after a password change on another device. That path cleared the token but never ran `onSignedOut`. The user lands on the login screen, but the previous person's favourites stay in memory. When the next person signs in, `FavoritesRepository.favorites()` returns those cached favourites without asking the server.

That's why it only happens sometimes: it needs an expired or revoked session rather than a normal logout.

I also found a related bug: `ProfileRepository` had no way to clear its cache, so the drawer header and Account screen kept showing the previous person's profile even after a normal logout.

## Fix
- **`session/SessionManager.kt`**: `logout()` and `sessionExpired()` now both go through one private `endSession()`. It clears the token and runs `onSignedOut`, so every way of signing out drops the previous user's data.
- **`profile/ProfileRepository.kt`**: added `clear()`.
- **`app/AppGraph.kt`**: `onSignedOut` now clears the profile as well as favourites and searches.

The public API listed in the README is unchanged.

## Tests added (`SessionTest.kt`)
- `switchingAccountsAfterSessionExpiryShowsNewData`: Ana's session is revoked on the server and Ben signs in. He sees his own favourites and profile, and the recent searches list is empty.
- `logoutClearsProfile`: after a normal logout, the next account sees its own profile.

One limitation remains: if the app loads data on background threads, a request started for the old user could finish after sign-out and refill the cache. This code runs everything on one thread, so that can't happen today. If you add async loading later, tie each cache to the session token it was loaded with.

I haven't committed anything.
