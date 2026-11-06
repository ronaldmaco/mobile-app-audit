# Case 012: analytics context written into shared enum constants

**Stack:** native Android (Kotlin). **Mode:** D (analytics review).

> Real case, anonymised.

## Report
None. Found while checking screen names sent with analytics events.

## What happened
To add context to analytics events, the code did `EventName.ITEM_OPENED.apply { title = item.title }` in more than ten places. An enum constant is a single shared object, so the assignment changed global state: the next event of the same type, from any screen, carried the previous item's title unless it was set again. Events from different screens leaked into each other.

## How to prove it
- Code: grep for `var` properties on enums or `object` singletons and for `.apply {` on them.
- Analytics debug view: open item A, then trigger the same event from a screen that does not set a title: it reports item A.

## Fix
Pass context as event parameters (a map or a data class built per event); make enum properties `val`.

## Skill coverage
| Gap | Added in 2.4 |
|---|---|
| Analytics checks covered timing and privacy, not how context is attached | Pattern "mutable shared constants as context" (enums, singletons, companion objects); analytics checklist item |
