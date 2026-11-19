The cause is that the app clears the previous person's data on a normal sign-out, but not when the server ends the session. I've fixed it, and all 5 tests now pass, including 2 new ones that failed before the fix.

**Why it only happens sometimes**

There are two ways a user gets sent back to the login screen:

1. **They tap Sign out** (`SessionManager.logout()`). This clears the cached favourites and recent searches, so the next account sees its own data. That's the case the existing test covered.
2. **The server rejects their token** (they changed their password on another device, or the token was revoked or expired). `ApiClient` gets a 401 and calls `SessionManager.sessionExpired()` (`session/SessionManager.kt:26`). That method only removed the token. It never called `onSignedOut`, so nothing was cleared.

In the second case the login screen appears as normal. The next person signs in, and `FavoritesRepository.favorites()` returns its in-memory cache (`favorites/FavoritesRepository.kt:11`) without asking the server. So the Home tab shows the previous person's favourites. Recent searches leak the same way.

**A second leak I found**

`ProfileRepository` caches the profile (used in the drawer header and Account screen) and had no way to clear it. So the previous person's name and profile showed after *any* account switch, even a normal sign-out.

**The fix** (small, no change to the public API)
- `session/SessionManager.kt`: `sessionExpired()` now also calls `onSignedOut()`, so both ways out of a session clear per-user data.
- `profile/ProfileRepository.kt`: added `clear()`.
- `app/AppGraph.kt`: the sign-out handler now clears the profile as well as favourites and recent searches.

**Tests** (`android/src/test/kotlin/SessionTest.kt`)
- `switchingAccountsAfterSessionExpiredShowsNewData`: Ana's favourites are cached, her token is revoked, a request gets a 401, then Ben signs in and should see only his own favourites, searches and profile. Before the fix, Ben got Ana's `["r10","r11"]`.
- `profileIsClearedOnLogout`: before the fix, Ben saw Ana's profile after a normal sign-out.

**Not covered**
- **Possible remaining race, not reproduced:** this layer runs synchronously. If the real app runs these calls in the background, a request started with the old token could finish after the new person signs in. That could fill the cache with the old person's data again, or a late 401 could sign the new person out. It's worth checking in the app's threading code; the usual guard is to drop any response whose token is no longer the current one.
- This repo only contains the Android data layer. If there's an iOS app or another app sharing this code, it should get the same check.
- I tested with the unit tests only, not on a device or a release build.

Nothing is committed. Tell me if you want a branch and commit, or the race guard added.
