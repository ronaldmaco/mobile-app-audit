# Results: 2.6.0 on Claude Code (e015-e018: store, security, autopsy, future risk)

| | |
|---|---|
| Date | 2026-11-17 |
| Agent / model | Claude Code, Claude Opus 5.5 (fresh subagent per run) |
| Runs | 4 evals x 2 conditions, 1 run each |
| With skill | The agent was told to read a copy of `SKILL.md` first and load its references on demand |
| Without skill | Same prompt and fixture, told not to use any skill |
| Isolation | Each run was told to read only its own work folder: a copy of the fixture (and, with the skill, a copy of the skill). The instruction was not enforced by a sandbox. Web access was allowed in both conditions. |
| Grading | One judge agent (same model family), blind: answers relabelled A/B at random (`blind-key.json`), mentions of the skill and its file names replaced. The orchestrating session spot-checked both failures against the raw answers. The maintainer has not reviewed them yet. |

## Score

| Eval | With skill | Without skill |
|---|---|---|
| e015 store readiness | ✅ 6/6 | ✅ 6/6 |
| e016 security review | ✅ 5/5 | ✅ 5/5 |
| e017 autopsy | ✅ 6/6 | ❌ 5/6 |
| e018 future risk | ✅ 6/6 | ❌ 5/6, one must_not hit |
| **Total** | **4/4 pass, 23/23 checks** | **2/4 pass, 21/23 checks, 1 must_not hit** |

Raw verdicts: `judge-verdicts.json`. Answers: `with/`, `without/` (five files had third-party URL schemes and example attacker domains rewritten for the anonymisation check; each says so in its first line).

## The two failures without the skill
- **e017, must 4 (why nothing caught it): borderline.** The answer says the tests only covered an empty avatar, and recommends treating removed fields as breaking changes, but it does not state that the contract change was never checked against the app as a reason it was missed. The judge marked it not met. A reviewer reading the recommendation as that explanation would count it, making the baseline 3/4.
- **e018, must 6 and must_not 1: clear.** The risk table has no source or Verified/Suspected status per item, and it states a Play 16 KB page-size deadline as fact with no source.

## How to read this
- **Small and favourable to the skill.** One run per condition. The four evals were written in the same session as the references they test (`store.md`, `security.md`, `autopsy.md`, `future-risk.md`), so their criteria match what those references ask for. Treat this as "the new references are followed and do no harm", not as a measured improvement.
- **e015 and e016 do not separate the conditions.** The model found every store and security issue without the skill. They stay as regression checks.
- **e017 and e018 differ on method, not on finding the bugs.** Without the skill, both answers found the main issues (the backend trigger in e017; the time bomb, pin and dependencies in e018). What they missed was the "why it was missed" step and the dated sources per risk, which are exactly what the new references require.
- **Unsupported claims in answers that passed.** The judge noted some in both conditions. With the skill: e015 says reviewers are "mostly in the US" without a source; e016 says to restrict the API key by the SHA-256 certificate fingerprint (the judge notes Google Cloud key restrictions for Android use SHA-1; not re-checked); e017 says a crash alert fired, which no fixture file shows. Without the skill: e015 dismisses the 4.8 exemptions; e016 gives two different dates for the rules lockout. None matched a must_not, so none changed a score.
- **Line numbers.** e018 must 1 asks for file and line; neither answer gave a number, both quoted the line. The judge counted both as met.
