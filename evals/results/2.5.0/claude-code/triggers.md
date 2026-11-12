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
- Limits: one run per prompt and description, 14 prompts written by the maintainer's agent, the same model family throughout. It shows the trigger now covers these requests; it does not show the skill improves the answers.
