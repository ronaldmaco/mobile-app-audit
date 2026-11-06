# Case 005: "Share analytics" off, still tracked; logout kept the last user

**Stack:** native iOS, an analytics SDK and a push SDK. Found in two sibling apps sharing a codebase. **Mode:** D (full audit).

> Real case, anonymised.

## Report
None. Found during the privacy part of a full audit.

## What happened
1. Turning off "Share analytics data" saved the preference but never told the analytics SDK, which identified users by email. Tracking continued.
2. Logout did not reset the analytics identity, so the next account on the device inherited the previous one.
3. Cached API responses lived in `Documents` (included in device backups), kept answers to write requests, and survived logout.
4. The previous user's "continue where you left off" card stayed after logout.
5. In the sibling app, the switch was flipped before the server confirmed and the event was logged even when the save failed.

## How to prove it
- For each privacy setting: follow the value from the switch to every SDK that should obey it.
- Read the cache folder on the simulator after logout (`xcrun simctl get_app_container booted <bundle> data`).

## Fix
- Opt out / opt in with the switch; reset analytics on logout.
- Cache moved to `Caches`, keeps only reads (and read-like POSTs), deleted on logout.
- Switch disabled while saving, reverted with an error on failure; event logged only on success.

## Skill coverage
| Gap | Added in 2.2 |
|---|---|
| "Logout clears analytics identity" existed, but no check that consent switches reach the SDK | Privacy: every consent switch is wired to every SDK it governs |
| No check of **where** caches live | Storage: per-user caches in a non-backed-up location, cleared on logout, no write responses cached |
