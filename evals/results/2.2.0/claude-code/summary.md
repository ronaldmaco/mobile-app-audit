# Results: 2.2.0 on Claude Code

| | |
|---|---|
| Date | 2026-11-06 |
| Agent / model | Claude Code, Claude Opus 5.5 (fresh subagent per run) |
| Runs | 8 evals x 2 conditions, 1 run each |
| With skill | The agent read `SKILL.md` first and loaded references on demand |
| Without skill | Same prompt and fixture, told not to use any skill |
| Grading | One judge agent (same model family), blind: answers relabelled A/B at random, mentions of the skill removed. The orchestrating Claude Code session then checked every failed item against the raw answer. Passes were not re-checked; the maintainer has not reviewed them yet. |

## Score

| Eval | With skill | Without skill | What the baseline missed |
|---|---|---|---|
| e001 dropped filter | ✅ 6/6 | ❌ 5/6 | No impact map: the mixed selection (linked + unlinked venues) was never considered |
| e002 write used as read | ✅ 3/3 | ✅ 3/3 | |
| e003 empty Home | ✅ 3/3 | ❌ 2/3 | Only an empty state; no Home fallback and no reason why Home may widen while an explicit filter must not |
| e004 SDK launch crash | ✅ 3/3 | ✅ 3/3 | |
| e005 consent and logout | ✅ 4/4 | ❌ 3/4 | Saw that the switch is not reverted, but not that the event must be logged only after a successful save |
| e006 sibling crash | ✅ 3/3 | ✅ 3/3 | |
| e007 spinners and images | ✅ 4/4 | ✅ 4/4 | |
| e008 not reproduced | ✅ 3/3 | ✅ 3/3 * | |
| **Total** | **8/8 pass, 29/29 checks** | **5/8 pass, 26/29 checks** | |

\* The judge failed the baseline on e008 ("asks only for what is missing"). On review the answer does ask for version, device/OS, edited fields and the error shown; the time was already known ("yesterday"). Overturned to pass. The judge's raw output is in `judge-verdicts.json` (A/B mapping in `blind-key.json`).

The judge flagged one unsupported claim in each e004 answer (the iOS SDK requirement comes from outside knowledge, not the fixture). The with-skill answer labelled it Suspected; the baseline stated it as fact.

## How to read this

- **The baseline is already strong.** Without the skill the model passed 26 of 29 checks. The skill does not "find bugs a strong model cannot". It adds specific practices: the impact map, the per-screen meaning of empty results, consent handling end to end, and keeping claims labelled Verified or Suspected.
- **These evals favour the skill.** They were written from the same cases that shaped it, so they measure whether the skill carries those lessons, not how it does on new bugs. New cases from other people are the real test.
- **One run per condition.** Answers vary between runs; a single run can flip a result either way.
- **Same model family as judge and reviewer.** Failures were re-checked by the orchestrating session, not by a person; passes were not re-checked. An independent human review is still pending.
- **Cost.** With the skill the answers were about 50% longer (about 16,300 vs 11,200 words in total).
