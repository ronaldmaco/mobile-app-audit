# Example report: "the venue filter is ignored"

This is what the skill hands back in **mode A (bug verification)**. It follows [case 001](../cases/001-dropped-filter-returns-everything.md), a real case anonymised: the domain, names, versions, ids, file names and counts are invented. The mechanism is the original one.

---

## 1. Summary

| | |
|---|---|
| Mode | A, bug verification (second report on the same area in two days) |
| Build audited | 3.4.1 (7), App Store; commit `abc1234` |
| Backend | Production CMS REST API |
| How it was checked | Code path, `git log` of the earlier fix, ground-truth oracle, catalog sweep, simulator on the release build |
| Not covered | Android app (excluded by the user); real device (simulator only) |

**Verdict:** the bug is real and verified. The previous fix caused it: it stopped sending id 0, so a user whose favourites are only unlinked venues sends **no venue filter**, and the API returns every event for the category. A second cause is in the CMS: 3 venue pages are unpublished and no event links to them.

## 2. Findings

```
[F1] [High] The venue filter is dropped for venues without events
Status: Verified
Where: EventsQuery.swift:12 (filters out id 0), events API
What the user sees: events at venues they did not pick
Cause: the day-1 fix removed id 0 from the request; with only unlinked venues selected the venue list is empty, and the API reads an empty list as "any venue"
Trigger: user's favourites are only venues whose CMS id is 0, plus any category
Evidence: oracle row "as the app sends it" = 180 events, same first 6 titles as the user's screenshot; row "intended" = 0
Impact map: 8 rows checked, 3 failing, 1 not tested (upgrade from 3.3.x: no old build available)
Fix: keep the selection; if every selected option in a category has no linked content, show the empty state instead of calling the API without that filter. No visual change beyond the existing empty state.
Test: EventsQueryTests.testUnlinkedVenuesOnly, testMixedSelection (fail before, pass after)
Also in: Home and Saved use the same query builder; both fixed by the same change. Android not checked (excluded).
Delivery: app build 3.4.2 (1)
```

```
[F2] [Medium] 3 venues have no page and no linked events
Status: Verified
Where: CMS, venues endpoint
What the user sees: those venues can be picked but never return events
Cause: venue pages unpublished; their events are linked to no venue
Evidence: catalog sweep over the full event list: 3 venue URLs answer 404; 0 events linked to them
Fix: CMS owner publishes the 3 pages and links the events. No app build needed.
Delivery: CMS, owner: content team
```

```
[F3] [Low] The unit test target had not built for years
Status: Verified
Where: project test target (wrong TEST_HOST, old deployment target, removed module)
What the user sees: nothing directly; "tests pass" meant nothing
Fix: repaired with approval; the target now builds and runs the new tests
```

## 3. Coverage (impact map for F1)

| Input | Consumer | State | Expected (oracle) | Result |
|---|---|---|---|---|
| Linked venues only | Events list | Signed in | only their events | OK |
| Unlinked venues only | Events list | Signed in | empty state | **F1**, fixed |
| Mixed linked + unlinked | Events list | Signed in | linked venues' events | **F1**, fixed |
| "No favourite venue" | Events list | Signed in | all events for the category | OK |
| Unlinked venues only | Filter sheet chips | Signed in | shown as selected | **F1**, fixed |
| Unlinked venues only | Home | Signed in | fallback per Home rules | OK |
| Any | Events list | Upgrade from 3.3.x | same as fresh | Not tested: no 3.3.x build |
| Any | Events list | Offline | error with Try again | OK |

## 4. Fix order and delivery

1. F1: app build 3.4.2 (1), above TestFlight's latest 3.4.1 (7).
2. F2: CMS change, no build. Once done, the empty state for those venues is replaced by real events automatically.
3. F3: already merged with F1.

## 5. Open questions and other owners

- Content team: publish the 3 venue pages and link their events (list in the audit log).
- Android: same query builder? Not checked; the user excluded it. Recommended next.

---

**Message to the reporter (in their language):** the fix is in version 3.4.2. Until the content team links the missing events, those venues show "no events yet" instead of unrelated ones.
