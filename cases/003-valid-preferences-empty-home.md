# Case 003: valid preferences, empty home

**Stack:** native iOS, headless CMS with a REST API. **Mode:** A (bug verification).

> Real case, anonymised: domain, names and counts are changed. The mechanism and proportions are the original ones.

## Report
Home showed a generic error in every list for some users, always the same ones.

## What happened
Home asked the API for items matching **all** of the user's preferences (two option lists, e.g. venue and category). The API needs an item to match one option of each list at once. About half of the possible pairs had no item, so for those users every Home list was empty and showed the error state, although nothing had failed.

## How to prove it
- Ground-truth oracle over the **whole combination space**: one request per pair of options, counting results. About half the pairs return 0.
- The affected users' preferences fall in those pairs.

## Fix
- Home falls back step by step: both preferences, then the first one only, then none, until the lists have content.
- The featured carousel is filled from the first step that has featured items (the API can leave it empty for the same filters).
- A failed fallback keeps what earlier steps found; an older load never replaces a newer one.

## Why this is not "hiding the user's input" (case 001)
The consumer decides. On a **filter/search screen** the user asked for something explicit, so an empty result is the honest answer. On a **curated Home** the preferences are a hint, and an empty Home is a broken product. The impact map must state the expected result per consumer, not one rule for all.

## Skill coverage
| Gap | Added in 2.2 |
|---|---|
| The oracle checked the reporter's combination, not the space of combinations | Backend: "combination sweep" |
| "Empty means error" was not separated from "empty means no data" | Pattern "empty result shown as an error" |
| One rule for empty results everywhere | Impact map note: expected result is per consumer (explicit filter vs curated feed) |
