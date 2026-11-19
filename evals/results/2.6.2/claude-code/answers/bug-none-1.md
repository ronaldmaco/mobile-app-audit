I found the cause and fixed it. All 5 tests pass with the fix; with the old code, the two new tests fail.

**Why it happens:** the Home tab's favourites are kept in memory (`FavoritesRepository`), and that copy is only cleared when the session ends through `SessionManager.logout()`. A session can also end because the server rejects the token, for example after a password change or a revoked session on another device. That path (`ApiClient` → `SessionManager.sessionExpired()`) removed the token and sent the user to the login screen, but it never cleared the cached data. Whoever signed in next on that device saw the previous person's favourites. That's why it only happens sometimes: a normal logout works, and only an expired session leaks.

`ProfileRepository` had a similar problem: its cached profile wasn't cleared on any sign-out, not even a normal logout. So the drawer header and Account screen could also show the previous user's profile.

**Fix:**
- `session/SessionManager.kt`: `logout()` and `sessionExpired()` now both go through one `endSession()`, which clears the token and calls `onSignedOut`. Any way a session ends now drops the per-user data.
- `profile/ProfileRepository.kt`: added `clear()`. `app/AppGraph.kt` now calls it on sign-out along with favourites and recent searches.
- `SessionTest.kt`: two new tests:
  - The server revokes Ana's session, a request gets the 401, then Ben signs in. The test checks that Ben sees his own favourites and profile, and that the recent searches list is empty.
  - Switching accounts after a normal logout shows the new user's profile.

The public API is unchanged. I haven't committed anything.
