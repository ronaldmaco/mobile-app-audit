# Case 001: a filter fix that made the list ignore the filter

**Stack:** native iOS, headless CMS with a REST API. **Mode:** A (bug verification), second report of the same area in two days.

> Real case, anonymised: domain, names, ids and counts are changed. The mechanism is the original one.

## Report
"I picked my two favourite venues and Concerts, but the list shows concerts everywhere. Only Concerts works."

## What happened
1. Day 1: the options API sent id 0 for a few venues that had no page in the CMS, the same id the app used for "No favourite venue". Selecting one reopened the settings with the wrong option selected.
2. The day 1 fix matched options by id or name (correct) and **stopped sending id 0 to the events API** (symptom patch).
3. Day 2: a user whose favourites were only those venues had an empty venue filter, which the API reads as "any venue". The list showed every concert.

## How it was found
- Ground-truth oracle: the API called with only the category filter returned exactly the user's screen.
- Catalog sweep: no event linked to those venues; a large share of events linked to no venue at all; the venue pages answered 404.
- `git log` showed the previous fix had removed those ids from the request.

## Fix
- Keep the user's selection everywhere (chips, filter sheet).
- If every selected option in a category has no linked content, show the empty state instead of calling the API without that filter. Mixed selections filter by the linked ids.
- Unit tests for each impact map row; the test target had not built for years (wrong test host, old deployment target, removed module) and was repaired.
- Backend owner asked to publish the missing pages and link their content; no new app build needed after that.

## What the skill missed and what changed (v2.1)
| Miss | Added |
|---|---|
| Fix verified on the reported symptom only | Impact map (inputs x consumers x states x data) |
| The fix hid user input | Principle "never hide the user's input" and the fix review gate |
| "Empty filter = any" not checked | Pattern + networking checklist item |
| Backend data never swept | Catalog sweep in `references/backend.md` |
| Screen not compared with the API | Ground-truth oracle |
| Earlier fix not reviewed | History check (2.2) |
| Tests "missing", not "broken" | Test setup check in inventory and preflight |
| Disk full mid-session | Environment preflight |
| Wrong row tapped during scroll | Device pitfalls |
| An outdated local copy of the skill loaded instead of the plugin | Install note in README: keep one copy only |
