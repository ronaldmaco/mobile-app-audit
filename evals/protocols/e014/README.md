# e014: the fix exists on the other platform

Protocol for eval e014. **Status: pilot planned, not run.** No results exist yet.

## What it measures
Whether the agent finds, **without being told**, that the same feature exists on iOS with the right behaviour, and uses it to fix Android. Built on the mechanism of [case 008](../../../cases/008-fixes-never-reached-the-other-platform.md). The Android evals e009-e013 pass with and without the skill, so this one is meant to be harder: the prompt never mentions iOS, platforms, HTTP codes or caching.

## Files
| Path | Visible to the agent | Committed |
|---|---|---|
| `evals/fixtures/e014/` (Kotlin/JVM project, `ios/PaymentService.swift`, visible tests, Gradle wrapper) | Yes: a copy of it is the whole workspace | Yes |
| `evals/protocols/e014/prompt.txt` (pasted as the user message) | Only as the prompt | Yes |
| `evals/protocols/e014/fixture.sha256` (frozen fixture manifest) | No | Yes |
| `evals/protocols/e014/score-run.sh` (prepare and score a run) | No | Yes |
| `evals/protocols/e014/runs-template.csv` (one row per run) | No | Yes |
| Hidden tests, rubric, expected outcomes | No | **No.** Kept in `.private/evals/e014/` (git-ignored) or any path set in `E014_PRIVATE` |

The fixture: `PaymentService.handleResponse(statusCode, body)` returns `Success` for any 2xx, and `ReceiptParser` fills missing or unreadable fields with defaults. The visible tests cover a valid response and a server error only, and pass on the defective code. The fixture `README.md` does not mention Swift, iOS or the expected behaviour; it says the public API of `PaymentService` must stay unchanged.

Hidden tests check public behaviour only (`PaymentService().handleResponse`). Mandatory: empty body, null body (204), whitespace-only body, empty id, amount 0, negative amount, malformed amount must fail; a valid response must still succeed; HTTP 500 must still fail. Bonus, recorded separately: a response with extra unknown fields. That one is a specification decision, not a certain bug.

## Running one run
1. `evals/protocols/e014/score-run.sh prepare <run_dir>` with `<run_dir>` **outside** this repository. It checks the fixture against `fixture.sha256` and copies it to `<run_dir>/workspace`.
2. Write `<run_dir>/model-config.json` with at least `condition` (`with` / `without`), `model`, the agent and version, and whether the skill was installed.
3. Start a fresh agent session whose working directory is `<run_dir>/workspace`, with the same model in both conditions. The skill is installed only for `with`; for `without` it is uninstalled (not only "told not to use it"). The workspace has no `CLAUDE.md` or `AGENTS.md`, and user-level instruction files and memories are disabled (for example a clean agent config directory). Paste `prompt.txt` unchanged. Save the full transcript.
4. `evals/protocols/e014/score-run.sh score <run_id> <run_dir>`. It:
   - builds `<run_dir>/scored` from a fresh copy of the frozen fixture plus **only** the agent's `android/src/main/**` (new files there and parser refactors are allowed);
   - records every other changed path as `out_of_scope_change`, and changes to visible tests or `ios/PaymentService.swift` as critical;
   - compiles once (`compiles_after_scope_filter`). A compile failure with out-of-scope changes present is reported as "failed due to out-of-scope dependency"; without them it is a plain failure of the agent's code;
   - copies the hidden tests in, runs them, and writes `<run_dir>/checksums.txt` (prompt, model config, rubric, each hidden test and each fixture file) and `<run_dir>/auto-row.csv`.
5. A human fills the remaining columns from the transcript and the rubric, and copies the row into the CSV.

## Metrics, per run (never combined into one score)
| Column | Rule |
|---|---|
| `iOS_discovery` | YES only if the transcript shows a tool call or command that outputs the contents of `ios/PaymentService.swift` (read, cat, view, or a grep that prints its lines). Mentioning iOS or guessing it exists is NO. |
| `divergence_identified` | YES/NO, judged separately: the agent correctly explains how the Android and iOS behaviour differ. |
| `mandatory_hidden_pass` | Passed/total mandatory hidden tests, or "failed due to out-of-scope dependency" / "failed: does not compile". |
| `bonus_pass` | Extra-fields test, separate. |
| `critical_failures` | Weakened or deleted visible tests, modified `ios/PaymentService.swift`, claimed tests it did not run (the last one is judged from the transcript). |
| `out_of_scope_change` | Paths changed outside `android/src/main/**`; not applied when scoring. |
| `rubric_points` | 0-4 from the private rubric: root cause, comparison of both implementations, user and payment risk, justified fix. |
| `checksums` | sha256 of the run's `checksums.txt`. |
| `compiles_after_scope_filter` | YES/NO. |

## Pilot plan (not executed)
- 5 independent runs per condition, fresh session each, same model, skill uninstalled for the baseline, no `CLAUDE.md` or `AGENTS.md` in the fixture.
- The pilot validates the **eval**, not the skill. Decision rules, fixed before any run:
  - Without skill, `iOS_discovery` and mandatory hidden tests both >= 4/5: ceiling effect, the eval is too easy. Harden it before any further use.
  - With skill, `iOS_discovery` < 4/5: investigate whether the skill's sibling check or the eval is at fault.
  - With skill >= 4/5 and without skill <= 2/5 on `iOS_discovery`: the eval discriminates; plan the full run.
  - Anything else: inconclusive; report raw counts only.
- Report raw counts per condition and the difference. No statistical significance and no general improvement are claimed from a pilot.
- Nothing goes into README or CHANGELOG until a human has reviewed the raw run data.

## Freeze
Before the first run, the fixture, hidden tests, rubric, prompt and model configuration are frozen. `fixture.sha256` is the frozen fixture manifest; `score-run.sh` refuses to prepare or score if the fixture differs from it. Per-file checksums of every input used are stored per run in `checksums.txt`. Keep the hidden tests and rubric unchanged across all runs of the pilot, and back up the private folder: it is not in git.

## Isolation limits
The hidden tests and rubric are outside the workspace and are not committed. An agent with an unrestricted shell can still read any absolute path the user can read. For real isolation, run the agent in a sandbox that only allows reading the workspace (or keep `E014_PRIVATE` on another machine or volume until scoring). The Gradle build downloads the Kotlin plugin and test libraries on first use; warm the Gradle cache before the runs so network timing does not differ between conditions.
