# Results: 2.5.0 on Claude Code (trigger eval)

| | |
|---|---|
| Date | 2026-11-12 |
| Agent / model | Claude Code 2.1.321 headless, Claude Opus 5.5, effort medium |
| Setup | Each prompt of `evals/triggers.json` once per description, fresh session in a macOS sandbox, workspace = copy of `fixtures/e014`, only this skill installed, capped at 150 s |
| Activated | The transcript shows the skill tool called for this skill, or its `SKILL.md` read |

## Score

| | Should trigger (8) | Should not trigger (6) |
|---|---|---|
| 2.4.1 description | 6/8 | 0/6 activated |
| 2.5.0 description | 8/8 | 0/6 activated |

## Per prompt

| Id | Expected | 2.4.1 | 2.5.0 |
|---|---|---|---|
| t01 | yes | NO | YES |
| t02 | yes | YES | YES |
| t03 | yes | YES | YES |
| t04 | yes | YES | YES |
| t05 | yes | YES | YES |
| t06 | yes | YES | YES |
| t07 | yes | NO | YES |
| t08 | yes | YES | YES |
| n01 | no | NO | NO |
| n02 | no | NO | NO |
| n03 | no | NO | NO |
| n04 | no | NO | NO |
| n05 | no | NO | NO |
| n06 | no | NO | NO |

## How to read this

- 2.4.1 missed t01 (the e014 prompt: a payment shown as successful for some server responses) and t07 (review payment code before a release). In the two e014 pilots the skill was never invoked on t01, so those pilots measured the model without the skill in both conditions.
- 2.5.0 adds to the description the case of users, testers or reviewers reporting something wrong, intermittent or still failing in a mobile app. It activated on all 8 and on none of the 6 unrelated requests.
- Limits: one run per prompt and description. The 14 prompts were written in the same session as the 2.5.0 description, whose examples (payments, checkout, login, screens that never load) match t01, t02, t06 and t08; the description was written before any trigger result was seen, but after the e014 pilots showed the t01 miss. 3 of 28 sessions were cut by the 150 s cap after the skill had loaded. It shows the description covers these prompts; it does not show it generalises or improves answers.

## Second check: fresh phrasings

Same agent, model, effort, sandbox and workspace as above, 2.5.0 description only. 8 prompts, 3 runs each, 24 sessions in random order (seed 434127), each capped at 4 turns, USD 0.30 and 150 s. a1 is the e014 prompt (same text as t01); a2-a5 are new bug reports written after the 2.5.0 description (a4 in Spanish); n1-n3 are ordinary code tasks on the same workspace (explain a class, add doc comments, rename a class).

| Id | Expected | Activated | First activation at tool call | Errors | Timeouts |
|---|---|---|---|---|---|
| a1 | yes | 2/3 | none, 2, 1 | 0 | 0 |
| a2 | yes | 3/3 | 1, 1, 1 | 0 | 0 |
| a3 | yes | 3/3 | 1, 1, 1 | 0 | 0 |
| a4 | yes | 3/3 | 1, 1, 1 | 0 | 0 |
| a5 | yes | 3/3 | 1, 1, 1 | 0 | 0 |
| n1 | no | 0/3 | none | 0 | 0 |
| n2 | no | 0/3 | none | 0 | 0 |
| n3 | no | 0/3 | none | 0 | 0 |

Totals: should load 14/15, should not load 0/9. Total cost USD 2.60.

- Limits: 3 runs per prompt, one model. The 4-turn cap ended 18 of 24 sessions, so "not activated" means not within 4 turns; the a1 miss used all 4.
- a1 overlaps with the description tuning (it is t01, whose miss the description was written to fix) and is the only prompt with no mobile wording (a2 only says "tapped"); it activated 2/3. a2-a5 were not used for tuning: 12/12.
- This measures whether the skill loads, not the quality of the answers.
