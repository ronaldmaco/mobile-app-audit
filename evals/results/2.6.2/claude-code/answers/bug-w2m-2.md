I found the cause and fixed it. All tests pass.

**Why it happens:** the app has two ways to end a session, and only one of them cleared the cached data.
- **Signing out** (`SessionManager.logout()`) clears the login token and calls `onSignedOut`, which empties the favourites and recent-searches caches.
- **Session expiry** (`SessionManager.sessionExpired()`) runs when the server rejects the token with a 401, for example after a password change elsewhere or a revoked token. It cleared only the token. The favourites list stayed in memory, so when someone else signed in on that device, the Home tab showed the previous user's favourites without fetching new ones.

That's why it only happens sometimes: it needs an expired session, not a normal sign-out.

The profile cache (`ProfileRepository`) had a worse version of the same problem: nothing ever cleared it, so the drawer header and Account screen could show the previous person's name even after a normal sign-out.

**The fix:**
- **`SessionManager.kt`:** sign-out and session expiry now both go through one private `endSession()`, which clears the token and calls `onSignedOut`. A new way of ending a session can't skip the cleanup by mistake. `login()` also clears the old user's data if it's called while someone is still signed in.
- **`ProfileRepository.kt`:** added `clear()`.
- **`AppGraph.kt`:** the sign-out cleanup now clears the profile cache too, alongside favourites and recent searches.

The public API listed in the README is unchanged.

**Tests:** I added three tests to `SessionTest.kt`:
- the session expires after favourites were loaded, then another user signs in;
- the session expires through a profile request while favourites are cached;
- the profile is reset after a normal sign-out.

All three fail on the old code and pass with the fix, and the existing tests still pass.

I haven't committed anything.
