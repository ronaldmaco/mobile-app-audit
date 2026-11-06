# Case 011: a random getter shows one item and opens another

**Stack:** native Android (Kotlin). **Mode:** C (home screen components).

> Real case, anonymised.

## Report
None. Found in passing while checking a promoted-item card fixed on iOS.

## What happened
The promoted item was a computed property that picked a random element from the eligible list on **every read**. The card read it once to render and again on tap, so the screen opened a different item than the one shown. When the list was empty (a user who already had every eligible item), `random()` threw and the screen crashed.

## How to prove it
- Code: a getter (or computed property) that calls `random()`, `shuffled()`, the clock or a counter returns a different value per read; find every reader.
- Device: account that owns every eligible item: the screen crashes on open.

## Fix
Pick once per screen load and keep the choice; use `randomOrNull()` and hide the card when there is nothing to show.

## Skill coverage
| Gap | Added in 2.4 |
|---|---|
| No check for non-deterministic getters | Pattern "random or time-based getter read more than once"; empty collection on `random()`/`first()` in the crash checklist |
