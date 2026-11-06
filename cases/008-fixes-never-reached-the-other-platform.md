# Case 008: the fixes never reached the other platform

**Stack:** native Android (Kotlin and legacy Java) sibling of a native iOS app, same backend. **Mode:** B/C (sibling check of fixes made on iOS).

> Real case, anonymised.

## Report
None. After a round of iOS fixes, the Android app (untouched for years) was checked against the same list.

## What happened
Most of the bugs fixed on iOS were still present on Android, including the sentinel-id filter that returned everything (case 001) and the empty Home (case 003). A few did not apply because Android's libraries already handled them (image loading cancelled on cell reuse, like state read at tap time). Several bugs existed only on Android (cases 009-012).

## How to prove it
- Take the iOS commits as the checklist: one row per fix, with the user-visible symptom.
- For each row, grep the Android code for the same endpoint, field or screen, read only that path, and mark it Verified present / Verified fixed / Not applicable with file:line.
- One read-only call to the options API confirmed that the sentinel ids were still served.

## Fix
Port the iOS behaviour where it exists, so both platforms act the same: same matching rule, same fallback chain, same empty states. No visual change.

## Skill coverage
| Gap | Added in 2.4 |
|---|---|
| "Siblings" said to check the other platform, but not how to do it cheaply | Sibling check method: the other platform's fix commits as the checklist, status per row, libraries that make a fix not applicable |
