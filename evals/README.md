# Evals

A small test set built from the real cases in `cases/`. Run it on every release to see whether the skill got better or worse.

## What is here
- `evals.json`: one entry per eval: the prompt, the mode, the fixture folder, and what the answer **must** and **must not** do.
- `fixtures/`: small, anonymised code and data that reproduce each case. They do not compile on their own; they are enough for a code-reading audit.
- `e008` has no case behind it on purpose: the right answer is "not reproduced, still Suspected". It checks that the skill does not invent a cause.

## How to run (any agent)
1. Open a fresh session in an agent with the skill installed (Claude Code, or any agent that reads `SKILL.md`).
2. Point it at one fixture folder, e.g. `evals/fixtures/e001`, and paste the eval's `prompt`.
3. Save the answer as `evals/results/<version>/<agent>/<id>.md`.
4. Grade it: every `must` met and no `must_not` hit = pass. Grade by hand, or give the answer plus the eval entry to a second model as a judge and spot-check its verdicts.
5. Add a row to the results table below.

Run each eval **without** the skill too, at least once per agent. The difference is what the skill adds.

## Results

| Version | Agent / model | With skill | Without skill | Notes |
|---|---|---|---|---|
| 2.2.0 | Claude Code, Opus 5.5 | 8/8 (29/29 checks) | 5/8 (26/29 checks) | e001-e008. 1 run each, blind judge, failures re-checked. [Details and limits](results/2.2.0/claude-code/summary.md) |
| 2.4.0 | Claude Code, Opus 5.5 | 5/5 (17/17 checks) | 5/5 (17/17 checks) | e009-e013 (Android). Too easy to separate the two; kept as a regression check. [Details](results/2.4.0/claude-code/summary.md) |

## Adding an eval
Every new case in `cases/` should get one: a fixture that reproduces it in as few lines as possible, the user's words as the prompt, and must / must_not items that a reviewer can check without guessing.
