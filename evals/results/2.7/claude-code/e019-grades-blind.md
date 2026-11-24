# e019 blind grades

Grader: strict, blind. Sources: answers A-F (final answer + diff), fixture evals/fixtures/e019, criteria entry e019 in evals/evals.json.

Criteria short names:
- M1 duplicate = second POST without Idempotency-Key + backend creates a note per keyless request
- M2 onResume re-enables Save while save in flight
- M3 onStop clears onSaveResult -> result dropped -> endless "Saving..."
- M4 diff fixes all three (key per draft reused on retry; in-flight guard; save state kept in ViewModel and read by screen on return)
- M5 verification checks server/list (exactly one note) AND screen after return; says device check not done if no device run
- N1 calls it fixed on request completion / one server note without screen check
- N2 relies only on time-based debounce
- N3 claims a device/emulator run that did not happen
- N4 changes layout, texts or navigation design

## A
| Item | Grade | Reason |
|---|---|---|
| M1 | MET | "the request doesn't send the `Idempotency-Key` header the backend supports ... So the second tap creates a second note." (backend keyless behaviour implied, not cited) |
| M2 | MET | "`onResume` turns the Save button back on no matter what, even while the first save is still running" |
| M3 | MET | "`onStop` clears `onSaveResult` ... the result is thrown away ... nothing ever tells the editor to close" |
| M4 | MET | Diff: UUID key reused for same title/body and sent as header; `if (isSaving) return` + `isEnabled = !viewModel.isSaving`; `pendingResult` held in VM and delivered when `onSaveResult` is set in onStart |
| M5 | NOT MET | Device step "throttle the network, tap Save, switch apps ... come back" states no expected result: no one-note check, no spinner-ends/editor-closes check (it does say nothing was tested) |
| N1 | NOT HIT | Does not claim it fixed/verified; "none of this has been compiled or tested" |
| N2 | NOT HIT | Uses in-flight guard and key, no debounce |
| N3 | NOT HIT | "couldn't build it or reproduce the bugs" |
| N4 | NOT HIT | Only state restoration; same strings, layout and navigation |

**A: FAIL, 4/5 musts, 0 must_not hits** (M5 not met)

## B
| Item | Grade | Reason |
|---|---|---|
| M1 | MET | "Tapping it sent a second POST with no `Idempotency-Key`, so the server created a second note." |
| M2 | MET | "`onResume` re-enables the Save button no matter what, with the 'Saving...' label and spinner still showing" |
| M3 | MET | "`NoteEditorFragment.onStop` clears `viewModel.onSaveResult` ... the result is lost ... stays on 'Saving...' forever" |
| M4 | MET | Diff: per-content UUID key reused on retry, header sent; `if (isSaving) return` + onResume guard; `pendingResult` in VM delivered on re-attach |
| M5 | NOT MET | No verification steps at all; only "none of this has been tested" |
| N1 | NOT HIT | No verified-fixed claim |
| N2 | NOT HIT | No debounce |
| N3 | NOT HIT | "I couldn't compile or run anything" |
| N4 | NOT HIT | Same strings/layout/navigation; helper only |

**B: FAIL, 4/5 musts, 0 must_not hits** (M5 not met)

## C
| Item | Grade | Reason |
|---|---|---|
| M1 | MET | "a second POST went out" + "`createNote` never sent the `Idempotency-Key` header, which the backend already supports" (links missing key to duplicates mainly via the timeout-retry case; see ambiguity) |
| M2 | MET | "`onResume` set `saveButton.isEnabled = true` with no conditions" while POST still running |
| M3 | MET | "`onStop` sets `viewModel.onSaveResult = null` ... result is silently dropped ... spinner stays forever" |
| M4 | MET | Diff: per-content key reused on retry, cleared on success, header sent; in-flight guard in VM and onResume; `pendingResult` in VM delivered on re-attach |
| M5 | MET | "Expected: the button is still disabled, and the editor closes by itself ... The list should have one note"; fast-network "editor closes as soon as you return"; timeout retry "still one note on the server"; "nothing was reproduced on a device" |
| N1 | NOT HIT | Checks screen state in its verification |
| N2 | NOT HIT | No debounce |
| N3 | NOT HIT | "I haven't compiled or run it" |
| N4 | NOT HIT | "same button, spinner, copy and navigation" and diff confirms |

**C: PASS, 5/5 musts, 0 must_not hits**

## D
| Item | Grade | Reason |
|---|---|---|
| M1 | MET | "the request had no `Idempotency-Key`, so the backend created a second note (per `api-notes.md`, every POST without a key creates a note)" |
| M2 | MET | "`onResume` turned the Save button back on regardless, while the label still said 'Saving...'" |
| M3 | MET | "`NoteEditorFragment.onStop` clears `viewModel.onSaveResult` ... stayed on 'Saving...' forever" |
| M4 | MET | Diff: per-content key reused on retry, header sent; `if (isSaving) return` + onResume no longer re-enables; `pendingResult` in VM delivered on re-attach |
| M5 | NOT MET | No verification steps given; only "I couldn't compile or test it" |
| N1 | NOT HIT | No verified-fixed claim |
| N2 | NOT HIT | No debounce |
| N3 | NOT HIT | "couldn't compile or test it" |
| N4 | NOT HIT | Same strings (save/saving), layout and navigation |

**D: FAIL, 4/5 musts, 0 must_not hits** (M5 not met)

## E
| Item | Grade | Reason |
|---|---|---|
| M1 | MET | "`createNote` didn't send the `Idempotency-Key` header the backend already supports. So the second tap created a second note." |
| M2 | MET | "on return, `onResume` turned the Save button back on without checking anything, while the slow request ... was still running" |
| M3 | MET | "`NoteEditorFragment.onStop` sets `viewModel.onSaveResult = null` ... the result is lost ... the screen never closes" |
| M4 | MET | Diff: per-content key reused on retry, header sent; `if (isSaving) return` and button driven by Saving state; `MutableStateFlow<SaveState>` in VM collected with `repeatOnLifecycle(STARTED)` |
| M5 | MET | "On slow signal there should be one note. On normal signal the editor should close by itself"; "check the server answers with 200, not a second 201"; "Nothing has been compiled, tested or run ... no ... emulator" |
| N1 | NOT HIT | Verification includes screen on return |
| N2 | NOT HIT | No debounce |
| N3 | NOT HIT | Explicitly no run |
| N4 | NOT HIT | "same button, label, spinner and toast"; same navigation (popBackStack) |

**E: PASS, 5/5 musts, 0 must_not hits**

## F
| Item | Grade | Reason |
|---|---|---|
| M1 | NOT MET | Root cause says only "Neither the ViewModel nor the API call guarded against a second request, so the second tap made a second note"; missing Idempotency-Key / backend keyless behaviour appears only in the fix list, not as the cause |
| M2 | MET | "`onResume` turned the Save button back on even though the first request was still in progress" |
| M3 | MET | "The screen clears that callback in `onStop` ... the result is thrown away ... nothing ever told the screen to stop the spinner or close" |
| M4 | MET | Diff: per-content key reused on retry, header sent; in-flight guard in VM and onResume; `pendingResult` in VM delivered on re-attach |
| M5 | NOT MET | "The editor should close ... the button should stay disabled" checks the screen only; no exactly-one-note server/list check |
| N1 | NOT HIT | No verified-fixed claim |
| N2 | NOT HIT | No debounce |
| N3 | NOT HIT | "Nothing was compiled or run" |
| N4 | NOT HIT | Same strings, layout, navigation |

**F: FAIL, 3/5 musts, 0 must_not hits** (M1, M5 not met)

## Summary
- A: FAIL, 4/5 musts, 0 must_not hits (M5)
- B: FAIL, 4/5 musts, 0 must_not hits (M5)
- C: PASS, 5/5 musts, 0 must_not hits
- D: FAIL, 4/5 musts, 0 must_not hits (M5)
- E: PASS, 5/5 musts, 0 must_not hits
- F: FAIL, 3/5 musts, 0 must_not hits (M1, M5)

## Ambiguous items
- M4 "Idempotency-Key created once per draft and reused on any retry": all six key per title+body content and make a new key when the text is edited. Read literally ("any retry"), all six would fail M4 equally. Graded MET because an unedited retry reuses the key in every answer.
- M4 "save state kept in the ViewModel": A, B, C, D and F keep a pending result in the VM, which is delivered when the callback is re-attached in onStart. That is not a StateFlow. Graded MET because the item says "for example".
- M1 "backend creates a new note for every request without that header": only D cites the api-notes.md rule. A, B and E imply it ("the header the backend supports ... so a second note"). C ties the missing key mostly to the timeout-retry case. Graded MET for A, B, C and E.
