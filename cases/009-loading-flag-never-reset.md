# Case 009: a loading flag that never resets, and a tab that shows the other tab

**Stack:** native Android (Kotlin). **Mode:** C (targeted audit of a list with tabs).

> Real case, anonymised.

## Report
None. Found while checking a tab-selection bug fixed on the sibling iOS app.

## What happened
A list screen with two tabs ("All" and "Saved") used two guards:
1. `isFetching` blocked new loads while one was running. It was set back to `false` only on success, so after the first failed request the screen never loaded again until the app restarted.
2. The response handler dropped answers whose query was no longer the current one.
Switching to "Saved" while "All" was loading: guard 1 refused the new query, so it was never sent, and the screen kept showing the "All" list under the "Saved" tab.

## How to prove it
- Code: every exit of the request (success, failure, cancel) must reset the flag; here only success does.
- Device: airplane mode, open the list (fails), back online, pull to refresh: nothing loads.
- Switch tabs during a slow load (network conditioner): the second tab shows the first tab's items.

## Fix
Reset the flag in a `finally`-style path for every outcome; on a tab change, cancel or supersede the running request instead of refusing the new one.

## Skill coverage
| Gap | Added in 2.4 |
|---|---|
| No check that loading flags reset on every outcome | Pattern "flag reset only on success"; networking checklist item |
| Two guards that together lose the newest request | Pattern "refuse-new plus drop-stale guards" |
