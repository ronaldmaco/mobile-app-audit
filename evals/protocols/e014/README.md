# e014: the fix exists on the other platform

Protocol for eval e014. **Status: v2; pilots 1 (v1) and 2 (v2) both hit the ceiling: the model found the fix without the skill. The skill (2.4.1) never loaded in any run, so neither pilot compared the model with and without it.** Kept as a regression check; no pilot has been run with 2.5.0.

## What it measures
Whether the agent finds, **without being told**, that the same feature exists on iOS with the right behaviour, and uses it to fix Android. Built on the mechanism of [case 008](../../../cases/008-fixes-never-reached-the-other-platform.md). The Android evals e009-e013 pass with and without the skill, so this one is meant to be harder: the prompt never mentions iOS, platforms, HTTP codes or caching.

## Files
| Path | Visible to the agent | Committed |
|---|---|---|
| `evals/fixtures/e014/` (Kotlin/JVM project with unrelated cart, profile and network files, an `ios/` tree with `ios/Checkout/PaymentService.swift`, visible tests, Gradle wrapper) | Yes: a copy of it is the whole workspace | Yes |
| `evals/protocols/e014/prompt.txt` (pasted as the user message) | Only as the prompt | Yes |
| `evals/protocols/e014/fixture.sha256` (frozen fixture manifest) | No | Yes |
| `evals/protocols/e014/score-run.sh` (prepare and score a run) | No | Yes |
| `evals/protocols/e014/runs-template.csv` (one row per run) | No | Yes |
| Hidden tests, rubric, expected outcomes | No | **No.** Kept in `.private/evals/e014/` (git-ignored) or any path set in `E014_PRIVATE` |

The fixture: `PaymentService.handleResponse(statusCode, body)` returns `Success` for any 2xx, and `ReceiptParser` fills missing or unreadable fields with defaults. The iOS version also rejects a 2xx answer whose `status` is `pending`, `authorized`, `requires_action`, `declined` or `failed`; nothing on the Android side mentions `status` (v2). The visible tests cover a valid response and a server error only, and pass on the defective code. The fixture `README.md` does not mention Swift, iOS or the expected behaviour; it says the public API of `PaymentService` must stay unchanged.

Hidden tests check public behaviour only (`PaymentService().handleResponse`). Mandatory: empty body, null body (204), whitespace-only body, empty id, amount 0, negative amount, malformed amount must fail; a valid response must still succeed; HTTP 500 must still fail. Mandatory since v2 (`PaymentServiceStatusTest`, reported separately by `score-run.sh` as "status rule only stated in iOS"): status `pending`, `authorized`, `requires_action`, `declined` must fail and `captured` must succeed. A fix built from the Kotlin code alone passes 10/14; only a fix that ports the iOS rule passes 14/14. Bonus, recorded separately: a response with extra unknown fields. That one is a specification decision, not a certain bug.

## Running one run
1. `evals/protocols/e014/score-run.sh prepare <run_dir>` with `<run_dir>` **outside** this repository. It checks the fixture against `fixture.sha256` and copies it to `<run_dir>/workspace`.
2. Write `<run_dir>/model-config.json` with at least `condition` (`with` / `without`), `model`, the agent and version, and whether the skill was installed.
3. Start a fresh agent session whose working directory is `<run_dir>/workspace`, with the same model in both conditions. The skill is installed only for `with`; for `without` it is uninstalled (not only "told not to use it"). The workspace has no `CLAUDE.md` or `AGENTS.md`, and user-level instruction files and memories are disabled (for example a clean agent config directory). Paste `prompt.txt` unchanged. Save the full transcript.
4. `evals/protocols/e014/score-run.sh score <run_id> <run_dir>`. It:
   - builds `<run_dir>/scored` from a fresh copy of the frozen fixture plus **only** the agent's `android/src/main/**` (new files there and parser refactors are allowed);
   - records every other changed path as `out_of_scope_change`, and any change under `ios/`, and deleted or changed lines in visible tests, as critical; tests that are only added are recorded as `visible_tests_extended`;
   - compiles once (`compiles_after_scope_filter`). A compile failure with out-of-scope changes present is reported as "failed due to out-of-scope dependency"; without them it is a plain failure of the agent's code;
   - copies the hidden tests in, runs them, and writes `<run_dir>/checksums.txt` (prompt, model config, rubric, each hidden test and each fixture file) and `<run_dir>/auto-row.csv`.
5. A human fills the remaining columns from the transcript and the rubric, and copies the row into the CSV.

## Metrics, per run (never combined into one score)
| Column | Rule |
|---|---|
| `iOS_discovery` | YES only if the transcript shows a tool call or command that outputs the contents of `ios/Checkout/PaymentService.swift` (read, cat, view, or a grep that prints its lines). Mentioning iOS or guessing it exists is NO. |
| `divergence_identified` | YES/NO, judged separately: the agent correctly explains how the Android and iOS behaviour differ. |
| `mandatory_hidden_pass` | Passed/total mandatory hidden tests, or "failed due to out-of-scope dependency" / "failed: does not compile". |
| `bonus_pass` | Extra-fields test, separate. |
| `critical_failures` | Weakened or deleted visible tests, modified files under `ios/`, claimed tests it did not run (the last one is judged from the transcript). |
| `out_of_scope_change` | Paths changed outside `android/src/main/**`; not applied when scoring. |
| `rubric_points` | 0-4 from the private rubric: root cause, comparison of both implementations, user and payment risk, justified fix. |
| `checksums` | sha256 of the run's `checksums.txt`. |
| `compiles_after_scope_filter` | YES/NO. |

## Pilot plan (not executed)
- 5 independent runs per condition, fresh session each, same model, skill uninstalled for the baseline, no `CLAUDE.md` or `AGENTS.md` in the fixture.
- The pilot validates the **eval**, not the skill. Decision rules, fixed before any run:
  - "Mandatory hidden tests" below means all mandatory tests pass (14/14 in v2).
  - Without skill, `iOS_discovery` and mandatory hidden tests both >= 4/5: ceiling effect, the eval is too easy. Harden it before any further use.
  - With skill, `iOS_discovery` < 4/5: investigate whether the skill's sibling check or the eval is at fault.
  - With skill >= 4/5 and without skill <= 2/5 on `iOS_discovery`: the eval discriminates; plan the full run.
  - Anything else: inconclusive; report raw counts only.
- Report raw counts per condition and the difference. No statistical significance and no general improvement are claimed from a pilot.
- Nothing goes into README or CHANGELOG until a human has reviewed the raw run data.

## Pilot 1 (v1), 2026-11-12
Raw data stay in the private folder; nothing here is a claim about the skill.
- Setup: 5 runs per condition, randomized order, Claude Code 2.1.321 headless, Claude Opus 5.5, effort medium, each run in a macOS sandbox (workspace only, network only to the model API), skill copied in only for `with`.
- With skill: `iOS_discovery` 5/5, mandatory 9/9 in 5/5 runs, `skill_activated` 0/5 (the skill was listed but never invoked). Without skill: `iOS_discovery` 5/5, mandatory 9/9 in 5/5. Difference: 0 and 0.
- Rule fired: ceiling effect. Causes: the 9-file workspace let every agent print all files, the iOS one included, with its first command; and the bug was fully fixable from the Kotlin code alone.
- v2 answers both: unrelated files and a nested `ios/` tree, and a mandatory rule that only the iOS code states.
- Open: the skill did not activate on this prompt in any run. That is a finding about the skill's trigger, not about this eval, and v2 does not change it.

## Pilot 2 (v2), 2026-11-12
Same setup as pilot 1, new randomized order.
- With skill: `iOS_discovery` 5/5, mandatory 14/14 in 5/5 runs (status rule only stated in iOS: 5/5 in every run), `skill_activated` 0/5. Without skill: `iOS_discovery` 5/5, mandatory 14/14 in 5/5 (status rule 5/5 in every run). Difference: 0 and 0.
- `skill_activated` 0/5: the with condition did not test the skill.
- Rule fired: ceiling effect again. Every run listed all files first and then read the payment code on both platforms, so the baseline already does the cross-platform comparison this eval was built to test.
- Decision: stop piloting e014. Keep it as a regression check. A discriminating version would need the other platform outside the workspace or a much larger repository; that is a new eval, not a third pilot.
- Every run in both pilots only added tests to the visible test file (0 lines removed). With the corrected rule, critical failures are 0/5 in both conditions of both pilots; all 20 runs are `visible_tests_extended`.

## Freeze
Before the first run, the fixture, hidden tests, rubric, prompt and model configuration are frozen. `fixture.sha256` is the frozen fixture manifest; `score-run.sh` refuses to prepare or score if the fixture differs from it. Per-file checksums of every input used are stored per run in `checksums.txt`. Keep the hidden tests and rubric unchanged across all runs of the pilot, and back up the private folder: it is not in git.

## Isolation limits
The hidden tests and rubric are outside the workspace and are not committed. An agent with an unrestricted shell can still read any absolute path the user can read. For real isolation, run the agent in a sandbox that only allows reading the workspace (or keep `E014_PRIVATE` on another machine or volume until scoring). The Gradle build downloads the Kotlin plugin and test libraries on first use; warm the Gradle cache before the runs so network timing does not differ between conditions.
