# Case 010: errors without a body vanish, and a failed request returns cached success

**Stack:** native Android (Kotlin), a third-party HTTP library with a response cache in the app. **Mode:** C (network layer).

> Real case, anonymised.

## Report
None. Found while checking the iOS fix "treat non-2xx responses as errors" on Android.

## What happened
The HTTP library already treated 4xx/5xx as failures, so the iOS bug did not apply as such. Two Android-only variants did:
1. The failure handler called `onError` only when the response had a body. A timeout or no connection has no body, so the screen got no callback at all: endless spinner.
2. On any failure, if the cache had an entry for the same key, the layer returned it **as a success**. For a write (like, save setting) the UI showed the change as saved although the server never received it.

## How to prove it
- Code: list every branch of the failure handler and what the caller receives in each.
- Device: airplane mode, then trigger a read and a write. The read spins forever; the write "succeeds".

## Fix
Every failure reaches the caller. A cached value may be shown for reads, marked as stale, together with the error; writes never fall back to the cache.

## Skill coverage
| Gap | Added in 2.4 |
|---|---|
| "Every failure reaches the UI" did not mention failures without a response body | Networking: transport errors (timeout, offline) have no body and must still reach the UI |
| Cache fallback not separated by method | Pattern "cache returned as success on a failed write" |
