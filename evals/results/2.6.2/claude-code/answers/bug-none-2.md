I fixed it, and all 5 tests now pass.

**Why it happened:** a session can end in two ways, and only one of them cleared the saved data:

- **Sign out:** clears the token and wipes the cached favourites and recent searches. This worked.
- **Session expired:** when the server rejects the token (password changed on another device, token revoked), `SessionManager.sessionExpired()` cleared the token but never cleared the caches. The next person to sign in was served the old user's favourites from memory, with no request to the server.

That's why it only happened sometimes: it needs an expired session followed by a sign-in with a different account. The profile (drawer header and Account screen) had a worse version of the same bug: it was never cleared, even on a normal sign-out. Recent searches also carried over after an expiry.

**The fix:**
- **`SessionManager.kt`:** `sessionExpired()` now clears per-user data just like `logout()`. `login()` also clears it, so a new session can never start with an earlier account's data, whatever path led there.
- **`ProfileRepository.kt`:** added `clear()`.
- **`AppGraph.kt`:** the profile is now cleared together with favourites and recent searches.

None of the public API listed in the README changed.

**Tests:** I added two to `SessionTest.kt`. One expires a session on the server, signs in as another user, and checks that favourites, profile and recent searches all belong to the new user. The other checks the profile after a normal sign-out and sign-in. Both fail on the old code and pass with the fix.

**Still open (I didn't fix this):** the real app loads data in the background. If a favourites request sent with the old token comes back after the next person has signed in, it can still put the old user's list into the cache. These synchronous tests can't show that. To close it, the cache should record which session it was loaded for and drop results from an earlier one. I can add that if you want.

I haven't committed anything.
