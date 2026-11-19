# Results: 2.6.2 head-to-head on Claude Code

Protocol: [`evals/protocols/h2h/README.md`](../../../protocols/h2h/README.md). Criteria and hidden test, now public:
[`evals/protocols/h2h/hidden/`](../../../protocols/h2h/hidden/); their sha256 match the ones published before the runs.

| | |
|---|---|
| Date | 2026-11-18 |
| Agent / model | Claude Code headless, Claude Opus 5.5, effort medium (`agent-cmd-example.txt`) |
| Runs | 3 tasks; 3 conditions (4 on the store task); 3 runs each; 30 sessions in 3 batches of 10, random order per batch (`seeds.txt`) |
| Conditions | `ours` = mobile-app-audit at `8129b11`; `w2m` = web-to-mobile-magic-plugin skills at `e6eb780`; `asr` = appstore-review-skill skills at `57948c1`; `none` = no skill (`sources.txt`) |
| Isolation | macOS sandbox per run: fresh git copy of the fixture, clean config dir, network only to the model API |
| Caps | 60 turns, USD 1.50 API-equivalent and 15 minutes per run. No run hit a cap; all 30 ended with `success`. API-equivalent total: USD 6.72 (`results.tsv`) |
| Grading | Bug task: hidden tests run automatically. All tasks: one judge agent per task and batch group (same model family), blind: labels shuffled, skill and file names scrubbed. Writing style can still reveal a condition. |

## Score

| Task | ours | w2m | asr | none |
|---|---|---|---|---|
| Bug: hidden tests (bug 2, sibling 2, guards 3) | 3/3 runs all pass | 3/3 | – | 3/3 |
| Bug: judge criteria (5 must, 2 must_not) | 3/3 pass | 3/3 | – | 3/3 |
| Unfinished app: pass | **3/3** | **2/3** | – | **3/3** |
| Unfinished app: planted items found, of 13 (with file) | 13 (10), 12 (11), 12 (10) | 12 (8), 12 (9), 12 (6) | – | 12 (10), 13 (10), 12 (10) |
| Store readiness (6 must, 2 must_not): pass | 3/3 | 3/3 | 3/3 | 3/3 |

Skills loaded (`results.tsv`): `ours` 9/9 runs; `asr` 3/3; `w2m` 3/3 on the unfinished app (its skill named
mobile-app-audit, a different skill from ours) and 0/6 on the bug and store tasks, so those six runs measure
the model alone.

## How to read this
- **No skill shows a measurable advantage with these fixtures.** Opus 5.5 without any skill solves all three
  tasks in every run. The bug and store tasks hit the ceiling in every condition.
- **The one failure:** a `w2m` run on the unfinished app named a file for only 6 planted items (must 1 needs 8).
  Its other two runs passed (one of them with 8, borderline). The `w2m` runs on this task also wrote a Markdown
  plan file into the project instead of answering in full; the judge did not count that as a code change.
- **Unsourced rules on the store task**, as noted by the judges: "App Review works mostly in US hours" or similar
  in 3 of 12 runs (`asr` 1, `w2m` 1, `none` 1, `ours` 0); a guidelines revision date without source (`asr` 3);
  camera purpose string cited under 2.1 instead of 5.1.1 (`none` 1, `w2m` 1); "companion content for a book is
  usually accepted" without source (`none` 1, `ours` 1). With 3 runs per condition this is a hint, not a result.
- **An early reading was noise:** after batch 1, only `ours` had found that signing out keeps the cart (U8); in
  batch 2 a `none` run found it too.

## Limits
- 3 runs per cell; one judge per task and batch group, same model family as the runs; no human review of all verdicts.
- The fixtures and criteria were written by the maintainer's agent, which knows this skill. The other projects'
  authors did not review them. `w2m` targets web-to-mobile migration; the bug task is outside its stated scope.
- The runs used the fixtures and protocol before they were committed (`repo-state.txt`); the files in this commit
  are the ones that ran, checked by the hashes in `frozen.sha256` for criteria, hidden test and prompts.
- `verdicts/` holds summaries transcribed from each judge's JSON (per-must reasons condensed to counts and notes).
- The `tests_added` column in `results.tsv` is always 0 because the scorer counted untracked files after they had
  been staged; the judges' verdicts show that every bug run added a regression test.
- Answers in `answers/` are the final messages only; full transcripts stay in the maintainer's private folder.
