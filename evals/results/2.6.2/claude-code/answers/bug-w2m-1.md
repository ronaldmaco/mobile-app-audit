I found the cause and fixed it. All tests pass, and the new test fails if the fix is removed.

**Why it only happens sometimes:** a session can end in two ways.

- **The user taps Sign out** (`SessionManager.logout()`): this clears the favourites and recent searches, so it works.
- **The server rejects the token** (a 401, for example after a password change on another device or a revoked token): `ApiClient` calls `SessionManager.sessionExpired()`. That method cleared the token but never ran the `onSignedOut` cleanup. The login screen appears, but `FavoritesRepository` still holds the last user's favourites in memory. Since `favorites()` returns that cached list without asking the server, the next person to sign in sees the previous user's Home tab.

So the bug only shows up when the switch happens after the session expired, not after a normal sign-out. There was a related problem too: the cached profile (drawer header and Account screen) was never cleared on either path.

**Fix:**
- `android/src/main/kotlin/session/SessionManager.kt`: `logout()` and `sessionExpired()` now both go through one private `endSession()`, which clears the token and runs `onSignedOut()`. A future way of ending a session can't skip the cleanup by accident.
- `android/src/main/kotlin/profile/ProfileRepository.kt`: added `clear()`.
- `android/src/main/kotlin/app/AppGraph.kt`: signing out now clears the profile as well as favourites and searches.

The public API listed in the README is unchanged; the only addition is `ProfileRepository.clear()`.

**Tests** (`android/src/test/kotlin/SessionTest.kt`):
- **Session expiry:** Ana's tokens are revoked on the server, the app gets a 401, then Ben signs in. The test checks that Ben sees his own favourites and profile and none of Ana's recent searches.
- **Profile after sign-out:** after a normal sign-out, the next user sees their own profile.

One limit: the repositories don't guard against a request that started under the old user and finishes after the new user signs in. That can't happen here because calls are synchronous, but if they become async you'll want to drop results that arrive after the session changed.
