# Results: 2.4.0 on Claude Code (Android evals e009-e013)

| | |
|---|---|
| Date | 2026-11-06 |
| Agent / model | Claude Code, Claude Opus 5.5 (fresh subagent per run) |
| Runs | 5 evals x 2 conditions, 1 run each |
| With skill | The agent read `SKILL.md` first and loaded references on demand |
| Without skill | Same prompt and fixture, told not to use any skill |
| Isolation | Each run saw only a copy of its fixture (and, with the skill, a copy of the skill), not the cases or the eval criteria |
| Grading | One judge agent (same model family), blind: answers relabelled A/B at random, mentions of the skill removed. The orchestrating Claude Code session spot-checked the baseline passes against the raw answers. The maintainer has not reviewed them yet. |

## Score

| Eval | With skill | Without skill |
|---|---|---|
| e009 fixes never reached Android | ✅ 5/5 | ✅ 5/5 |
| e010 loading flag never reset | ✅ 3/3 | ✅ 3/3 |
| e011 errors without body, cache as success | ✅ 3/3 | ✅ 3/3 |
| e012 random getter | ✅ 3/3 | ✅ 3/3 |
| e013 shared enum mutated | ✅ 3/3 | ✅ 3/3 |
| **Total** | **5/5 pass, 17/17 checks** | **5/5 pass, 17/17 checks** |

No unsupported claims were found in any answer. Raw verdicts are in `judge-verdicts.json` (A/B mapping in `blind-key.json`).

## How to read this

- **These evals do not separate the two conditions.** Each Kotlin fixture holds one clear bug in a few lines of code, and the model finds it without help. They are useful as a regression check (the skill still gets them right) but say nothing about what the skill adds on Android.
- **What differed was the shape of the answer, not the result.** With the skill, answers added impact maps, a review gate and Verified/Suspected labels, and were about 50% longer (about 8,700 vs 5,800 words), the same ratio as in 2.2.0.
- **Next step for the eval set:** harder Android fixtures where the bug only shows in a combination (for example two screens sharing state, or a fix present on one platform and half-ported on the other), so the impact map and the sibling check have something to catch.
- Same limits as 2.2.0: one run per condition, the same model family as the judge, and evals written from the cases that shaped the skill.
