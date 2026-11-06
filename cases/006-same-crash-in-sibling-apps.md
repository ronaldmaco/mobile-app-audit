# Case 006: the same keyboard crash in two sibling apps

**Stack:** native iOS (UIKit). Two apps from the same codebase. **Mode:** D (full audit), sibling check.

> Real case, anonymised.

## Report
None. Found while reading shared UI code during a full audit.

## What happened
A shared keyboard-handling helper saved the scroll view's content size when the keyboard appeared and force-unwrapped it when it hid. iOS can post "will hide" without "will show" (hardware keyboard, a sheet presented over a form), so the unwrap crashed.

## How to prove it
- Code: the hide handler unwraps a value only the show handler sets.
- Device: simulator with the hardware keyboard connected, or present a sheet over the form and dismiss it.
- Sibling check: the same file exists in the second app, with the same bug.

## Fix
Restore the content size only if one was saved. Same change in both apps.

## Skill coverage
| Gap | Added in 2.2 |
|---|---|
| Lifecycle checklist did not mention unbalanced system notifications | Pattern "unpaired events" (hide without show, resume without pause, end without begin) |
| Sibling check worked as written | none, kept as a positive eval |
